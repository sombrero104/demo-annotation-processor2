package me.sombrero;

import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * 아래 프로세서를 등록하기 위해서는 manifest 파일을 등록해야 한다.
 * java 디렉토리 밑에 resources 디렉토리를 생성하고 모듈 세팅에서 resources로 등록해준 다음..
 * resource 디렉토리 밑에 META-INF/services 디렉토리를 생성해 준다.
 * 그리고 Processor의 패키지인 javax.annotation.processing.Processor 파일을 생성하고,
 * 내가 만든 프로세서의 패키지명을 입력해준다. (me.sombrero.MagicMojaProcessor)
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
@AutoService(Processor.class)
public class MagicMojaProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Magic.class.getName());
    }

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
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Magic.class);
        for(Element element : elements) {
            Name elementName = element.getSimpleName();
            if(element.getKind() != ElementKind.INTERFACE) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Magic annotation can not be used on " + elementName);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + elementName);
            }
        }
        return true;
    }

}
