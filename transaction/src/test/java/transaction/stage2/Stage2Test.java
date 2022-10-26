package transaction.stage2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 트랜잭션 전파(Transaction Propagation)란? 트랜잭션의 경계에서 이미 진행 중인 트랜잭션이 있을 때 또는 없을 때 어떻게 동작할 것인가를 결정하는 방식을 말한다.
 * <p>
 * FirstUserService 클래스의 메서드를 실행할 때 첫 번째 트랜잭션이 생성된다. SecondUserService 클래스의 메서드를 실행할 때 두 번째 트랜잭션이 어떻게 되는지 관찰해보자.
 * <p>
 * https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#tx-propagation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Stage2Test {

    private static final Logger log = LoggerFactory.getLogger(Stage2Test.class);

    @Autowired
    private FirstUserService firstUserService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /**
     * 생성된 트랜잭션이 몇 개인가? 왜 그런 결과가 나왔을까?
     */
    @DisplayName("REQUIRED = 기존 트랜잭션에 참여한다. 총 생성된 트랜잭션은 1개.")
    @Test
    void testRequired() {
        final var actual = firstUserService.saveFirstTransactionWithRequired();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithRequired");
    }

    /**
     * 생성된 트랜잭션이 몇 개인가? 왜 그런 결과가 나왔을까?
     */
    @DisplayName("REQUIRED_NEW = 기존 트랜잭션과 독립적인 새로운 트랜잭션을 시작한다. 총 생성된 트랜잭션은 2개.")
    @Test
    void testRequiredNew() {
        final var actual = firstUserService.saveFirstTransactionWithRequiredNew();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(2)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithRequiresNew",
                        "transaction.stage2.FirstUserService.saveFirstTransactionWithRequiredNew");
    }

    /**
     * firstUserService.saveAndExceptionWithRequiredNew()에서 강제로 예외를 발생시킨다. REQUIRES_NEW 일 때 예외로 인한 롤백이 발생하면서 어떤 상황이 발생하는
     * 지 확인해보자.
     */
    @DisplayName("REQUIRED -> REQUIRED_NEW(정상) -> REQUIRED(예외) = REQUIRED_NEW는 별도 트랜잭션이므로 커밋되었다")
    @Test
    void testRequiredNewWithRollback() {
        assertThat(firstUserService.findAll()).hasSize(0);

        assertThatThrownBy(() -> firstUserService.saveAndExceptionWithRequiredNew())
                .isInstanceOf(RuntimeException.class);

        assertThat(firstUserService.findAll()).hasSize(1);
    }

    /**
     * FirstUserService.saveFirstTransactionWithSupports() 메서드를 보면 @Transactional이 주석으로 되어 있다. 주석인 상태에서 테스트를 실행했을 때와 주석을
     * 해제하고 테스트를 실행했을 때 어떤 차이점이 있는지 확인해보자.
     */
    @DisplayName("SUPPORT = 기존 트랜잭션 존재 시 참여, 없을 시 트랜잭션 없이 수행.")
    @Test
    void testSupports() {
        final var actual = firstUserService.saveFirstTransactionWithSupports();

        log.info("transactions : {}", actual);

        // saveFirst 에서 Transactional을 주석처리할 경우, firstTransactionName이 null이됨.
        // 그리고 secondTransactionName은 SecondUserService 기준으로 생성됨.
        // 단, 트랜잭션 이름이 할당되었을 뿐, 실제 트랜잭션이 열린 것은 아니다.
        // 따라서 isActualTransactionActive 결과가 false, false 가 된다.

        // 주석 처리 시 이 코드로 통과함
//        assertThat(actual)
//                .hasSize(1)
//                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithSupports");

        // saveFirst 에서 Transactional을 살려둘 경우, firstTransactionName에 FirstUserService 기준으로 생성됨.
        // 그리고 secondTransactionName은 first와 동일하게 되는데, SUPPORTS가 기존 트랜잭션 존재할 경우 참여하는 옵션이기 때문.
        // 따라서 isActualTransactionActive 결과가 true, true 가 되고, 트랜잭션 이름이 First로 동일하다.

        // 주석 해제 후에는 이 코드로 통과함
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithSupports");
    }

    /**
     * FirstUserService.saveFirstTransactionWithMandatory() 메서드를 보면 @Transactional이 주석으로 되어 있다. 주석인 상태에서 테스트를 실행했을 때와
     * 주석을 해제하고 테스트를 실행했을 때 어떤 차이점이 있는지 확인해보자. SUPPORTS와 어떤 점이 다른지도 같이 챙겨보자.
     */
    @DisplayName("MANDATORY = 기존 트랜잭션 존재 시 참여, 없을 시 예외 던짐 : IllegalTransactionStateException")
    @Test
    void testMandatory() {
        final var actual = firstUserService.saveFirstTransactionWithMandatory();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithMandatory");
    }

    /**
     * 아래 테스트는 몇 개의 물리적 트랜잭션이 동작할까?
     * <p>
     * FirstUserService.saveFirstTransactionWithNotSupported() 메서드의 @Transactional을 주석 처리하자.
     * <p>
     * 다시 테스트를 실행하면 몇 개의 물리적 트랜잭션이 동작할까?
     * <p>
     * 스프링 공식 문서에서 물리적 트랜잭션과 논리적 트랜잭션의 차이점이 무엇인지 찾아보자.
     */
    @DisplayName("NOT_SUPPORTED = 트랜잭션 없이 수행. 기존 트랜잭션 있을 시 중단시킴.")
    @Test
    void testNotSupported() {
        final var actual = firstUserService.saveFirstTransactionWithNotSupported();

        // 트랜잭션 없이 수행하더라도 트랜잭션 이름은 할당하기에 2개가 반환된다.
        // 단, isActualTransactionActive는 true, false가 반환된다.

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(2)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNotSupported",
                        "transaction.stage2.FirstUserService.saveFirstTransactionWithNotSupported");
    }

    /**
     * 아래 테스트는 왜 실패할까?
     * <p>
     * FirstUserService.saveFirstTransactionWithNested() 메서드의 @Transactional을 주석 처리하면 어떻게 될까?
     */
    @DisplayName("NESTED = 기존 트랜잭션 없으면 REQUIRED처럼 동작, 있으면 세이브 포인트처럼 동작. NESTED 내 예외 발생해도 NESTED 호출 전까지만 롤백")
    @Test
    void testNested() {
        final var actual = firstUserService.saveFirstTransactionWithNested();

        // https://stackoverflow.com/questions/12390888/differences-between-requires-new-and-nested-propagation-in-spring-transactions
        // REQUIRED_NEW vs NESTED
        // NEW 는 완전히 독립된 트랜잭션을 시작한다. 따라서 부모 트랜잭션에서 예외가 발생해도 NEW가 정상처리됐다면 그건 그것대로 반영된다.
        // NESTED는 부모 트랜잭션이 없었을 경우엔 REQUIRED 처럼 동작한다.
        // NESTED는 부모 트랜잭션이 있었을 경우엔, 부모 트랜잭션이 예외 발생 시 NESTED도 반영되지 않는다.
        // NESTED는 부모 트랜잭션이 있었을 경우, NESTED의 예외 발생이 부모 트랜잭션에 영향을 미치지 않는다. 부모 O, 자식 X 일 경우 부모는 반영된다.
        // 그러나 JPA(Hibernate)에서는 NESTED 옵션을 지원하지 않는다.

        // 주석처리할 경우
        // 기존 트랜잭션이 없으므로, SecondUserService#saveSecondTransactionWithNested가 REQUIRED 처럼 동작
        // FirstUserService에 Transactional 애너테이션부터가 없었으므로 트랜잭션 이름도 1개만 생성됨
        // 새로운 트랜잭션을 시작하므로 isActualTransactionActive는 false, true

        log.info("transactions : {}", actual);
//        assertThat(actual)
//                .hasSize(1)
//                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNested");

        // 주석처리하지 않을 경우
        // 기존 트랜잭션이 있으므로 SecondUserService의 세이브 포인트로 동작.
        // 즉, SecondUserService 에서 예외가 발생하더라도, FirstUserService 의 커밋에 영향이 없다.
        // NestedTransactionNotSupportedException : JpaDialect does not support savepoints
//        assertThat(actual)
//                .hasSize(1)
//                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithNested");
    }

    /**
     * 마찬가지로 @Transactional을 주석처리하면서 관찰해보자.
     */
    @DisplayName("NEVER = 트랜잭션 없이 수행, 있으면 예외 던짐")
    @Test
    void testNever() {
        final var actual = firstUserService.saveFirstTransactionWithNever();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNever");
    }
}
