
<br/>

## < 더 자바, 코드를 조작하는 다양한 방법 >
<br/>

### 1. JVM 구조
<br/>

### 2. 바이트 코드 조작
ASM(비지터 패턴) 또는 Javassist, ByteBuddy(권장, ASM 사용, API 사용 편리)
<br/><br/>

### 3. 리플렉션 API
클래스 정보 참조(메소드, 필드, 생성자, 제네릭정보, …) <br/>
리플렉션 API를 다른 기술과도 같이 사용. <br/>
private 필드, 메소드도 접근 가능. 대신 성능에 이슈가 있을 수 있음. 
<br/><br/>

### 4. 다이나믹 프록시 기법
자바의 Proxy(리플렉션의 일환), CGlib, ByteBuddy(권장)
<br/><br/>

### 5. 애노테이션 프로세서
AbstractProcessor, Processor, Filer<br/>
, ProcessingEnvironment, RoundEnvironment, TypeElement <br/>
등에 대한 API 학습 필요.

#### [ 같이 사용하면 도움되는 라이브러리 ]
AutoService, Javapoet

#### [ 장점 ]
런타임 비용이 없어진다.
자바 에이전트를 사용하는 바이트 조작의 경우에는 런타임에 발생.<br/>
때문에 최초에 자바 애플리케이션이 구동될 때, 클래스가 로딩되는 시점에 추가적인 비용이 발생.<br/>
하지만 애노테이션 프로세서는 애플리케이션이 구동되는 시점인 런타임이 아니라<br/>
컴파일할 때 이미 조작해 놓은 상태이고 런타임 때에는 이미 조작된 것을 읽어서 사용만 함.<br/>
때문에 추가적인 비용이 없음.

#### [ 단점 ]
현재로서는 public한 API가 없다.<br/>
롬복처럼 약간의 해킹을 통해 기존 코드를 고치는 방법밖에 없다.

#### [ 오버라이드 애노테이션에서의 애노테이션 프로세서 사용 ]
https://stackoverflow.com/questions/18189980/how-do-annotations-like-override-work-internally-in-java/18202623

<br/><br/><br/>

