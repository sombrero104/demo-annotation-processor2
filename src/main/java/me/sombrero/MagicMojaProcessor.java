package me.sombrero;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

/**
 * 아래 프로세서를 등록하기 위해서는 manifest 파일을 등록해야 한다.
 * java 디렉토리 밑에 resources 디렉토리를 생성하고 모듈 세팅에서 resources로 등록해준 다음..
 * resource 디렉토리 밑에 META-INF/services 디렉토리를 생성해 준다.
 * 그리고 Processor의 패키지인 javax.annotation.processing.Processor 파일을 생성하고,
 * 내가 만든 프로세서의 패키지명을 입력해준다. (me.sombrero.MagicMojaProcessor)
 *      => 사실 이 부분은 안해도 컴파일하면 @AutoService(Processor.class)가 자동으로 파일을 만들어준다.
 * 그리고 mvn clean install 하면 해당 매니페스트 파일을 읽는 시점에는 MagicMojaProcessor가 컴파일되어 있지 않으므로,
 * 잠시 매니페스트 파일의 me.sombrero.MagicMojaProcessor 부분을 주석처리한 다음 mvn clean install을 실행.
 * 그리고 다시 주석을 풀고 mvn install 을 실행한다. 로
 * (clean을 하지 않고 install하면 이전에 컴파일된 클래스가 그대로 남아있으므로..)
 *
 * 위의 방법으로 등록하는 것은 너무나도 번거롭기 때문에
 * 등록을 도와주는 또 다른 애노테이션 프로세서인 AutoService(서비스 프로바이더 레지스트리 생성기)를 사용하는 것을 권장한다.
 * AutoService는 컴파일할 때에 매니페스트 파일을 자동으로 생성해준다.
 *
 * AutoService 사용법은..
 * AutoService 의존성 추가 후
 * 내가 만든 프로세서에 @AutoService(Processor.class) 애노테이션을 붙여주면 된다.
 * (이전에 만들었던 resources 디렉토리는 지운다. 지금은 그냥 남겨둠..)
 * 그리고 다시 mvn clean install 한 후 압축된 jar 파일을 열어보면
 * 위에서 만들었던 매니페스트 파일과 똑같은 파일이 자동으로 생성되어 있는 것을 확인할 수 있다.
 */

// @AutoService(Processor.class)를 붙이고 컴파일하면
// 자동으로 target의 resources/META-INF/services 밑에 파일을 만들어준다.
// (참고로 src의 resources/META-INF/services 밑에 있는 파일은 내가 그냥 만들어둔 것..)
@AutoService(Processor.class) // 현재 이 프로세서를 등록하기 위해 매니페스트 파일을 자동으로 생성해 주는 라이브러리.
public class MagicMojaProcessor extends AbstractProcessor {

    // 지원하는 애노테이션 종류
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Magic.class.getName());
    }

    // 지원하는 자바 버전 (현재는 최근 버전의 자바 지원하도록 설정.)
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * true를 리턴하면 여기에서 해당 애노테이션을 처리 완료했다는 뜻.
     * 다음 프로세서에게 더이상 이 애노테이션을 처리하라고 부탁하지 않음.
     * 경우에 따라서 다음 프로세서에서도 처리가 필요한 경우에는 false를 리턴.
     * (이 예제에서는 Magic에 특화된 애노테이션을 처리하므로 true를 리턴함.)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // @Magic 애노테이션이 붙어있는 엘리먼트들을 불러온다.
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Magic.class);
        for(Element element : elements) {
            Name elementName = element.getSimpleName();
            // @Magic 애노테이션이 붙어있는 엘리먼트가 인터페이스가 아닐 경우 메세지 처리.
            if(element.getKind() != ElementKind.INTERFACE) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Magic annotation can not be used on " + elementName);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + elementName);
            }

            /**
             * 롬북처럼 해당 애노테이션 사용 시 새로운 소스코드를 생성해내기.
             * JavaPoet 라이브러리를 사용. JavaPoet 의존성을 추가해준다.
             */
            TypeElement typeElement = (TypeElement)element;
            ClassName className = ClassName.get(typeElement);

            // 토끼를 꺼내는.. pullOut이라는 이름으로 메소드를 만들어보자.
            MethodSpec pullOut = MethodSpec.methodBuilder("pullOut")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String.class)
                    .addStatement("return $S", "Rabbit!!")
                    .build();

            // MagicMoja라는 클래스를 만들고 위에서 만든 메소드를 추가한다.
            TypeSpec magicMoja = TypeSpec.classBuilder("Magic" + elementName)
                    .addModifiers(Modifier.PUBLIC)
                     .addSuperinterface(className)
                    .addMethod(pullOut)
                    .build();

            // 실제 소스파일 만들기.
            Filer filer = processingEnv.getFiler();
            try {
                JavaFile.builder(className.packageName(), magicMoja)
                        .build()
                        .writeTo(filer); // 위에서 만든 클래스 파일을 write 한다.
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + e);
            }
        }
        return true;
    }

}
