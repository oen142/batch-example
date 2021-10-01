스프링 배치 이해

- 큰 단위의 작업을 일괄 처리
- 대부분 처리량이 많고 비 실시간성 처리에 사용
    - 대용량 데이터 계산, 정산, 통계, 데이터베이스, 변환 등
- 컴퓨터 자원을 최대로 활용
    - 컴퓨터 자원 사용이 낮은 시간대에 배치를 처리하거나
    - 배치만 처리하기 위해 사용자가 사용하지 않는 또다른 컴퓨터 자원을 사용
- 사용자 상호작용으로 실행되기 보단, 스케줄러와 같은 시스템에 의해 실행되는 대상
    - 예를 들면 매일 오전 10시에 배치 실행, 매주 월요일 12시 마다 실행
    - crontab, jenkins ...

- 배치 처리를 하기 위한 Spring Framework 기반기술
    - Spring에서 지원하는 기술 적용가능
    - DI, AOP , 서비스 추상화
- 스프링 배치의 실행 단위인 Job과 Step
- 비교적 간단한 작업(tasklet) 단위 처리와, 대량 묶음 ( Chunk ) 단위  처리


**스프링 배치는 잡타입의 빈이 생성되면 잡런처가 잡을 실행 잡은 스텝을 실행**

**잡 리파지토리는 디비 메모리에 배치가 실행할수 있도록 배치 메타데이터 관리**

- Job은 JobLaucher에 의해 실행
- Job은 배치의 실행 단위를 의미
- Job은 N개의 Step을 실행할 수 있으며, 흐름(Flow)를 관리 할 수 있다.
    - 예를 들면 , A Step 실행 후 조건에 따라 B Step 또는 C step을 실행 설정

- Step은 Job의 세부 실행 단위이며, N개가 등록돼 실행 된다.
- Step의 실행 단위는 크게 2가지로 나눌 수 있다.
    - Chunk 기반 : 하나의 큰 덩어리를 n개씩 나눠서 실행
        - 청크는 페이징 처리
        - 만개의 데이터 한번에 처리 한번에 처리해도 컴퓨터자원에 문제없으면 tasklet 사용
    - Task 기반 : 하나의 작업 기반으로 실행
- Chunk 기반 Step은 ItemReader, ItemProcessor, ItemWriter가 있다.
    - 여기서 Item은 배치 처리 대상 객체를 의미한다.
- **ItemReader**는 배치 처리대상 객체를 읽어 ItemProcessor 또는 ItemWriter에게 전달한다.
    - 예를 들면, 파일 또는 DB에서 데이터를 읽는다.
- **ItemProcessor** input객체를 output 객체로 filtering 또는 processing 해 **ItemWriter에게 전달**한다.
    - 예를 들면 ItemReader에서 읽은 데이터를 수정 또는 ItemWriter 대상인지 filtering 한다.
    - ItemProcessor는 optional하다.
    - ItemProcessor가 하는 일을 ItemReader 또는 ItemWriter가 대신할 수 있다.
- ItemWriter는 배치 처리 대상 객체를 처리한다.
    - 예를 들면, DB update를 하거나 처리 대상 사용자에게 알림을 보낸다.

- 배치 실행을 위한 메타 데이터가 저장되는 테이블
- BATCH_JOB_INSTANCE
    - Job이 실행되며 생성되는 최상위 계층의 테이블
    - job_name과 job_key를 기준으로 하나의 row가 생성되며, 같은 job_name과 job_key가 저장될수 없다.
    - job_key는 BATCH_JOB_EXECUTION_PARAMS에 저장되는 Parameter를 나열해 암호화해 저장한다
- BATCH_JOB_EXECUTION
    - Job이 실행되는 동안 시작/종료 시간, job의 상태등을 관리
- BATCH_JOB_EXECUTION_PARAMS
    - Job을 실행하기 위해 주입된 parameter 정보 저장
- BATCH_JOB_EXECUTION_CONTEXT
    - Job이 실행되며 공유해야할 데이터를 직렬화해 저장
- BATCH_STEP_EXECUTION
    - Step이 실행되는 동안 필요한 데이터 또는 실행된 결과를 저장
- BATCH_STEP_EXECUTION_CONTEXT
    - Step이 실행되며 공유해야할 데이터를 직렬화해 저장