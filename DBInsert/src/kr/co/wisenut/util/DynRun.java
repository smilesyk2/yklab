package kr.co.wisenut.util;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2010. 3. 23
 * Time: 오후 3:46:11
 *
 * 동적 실행. 라이브러리에 정의된 class 를 호출. 매소드 실행결과를 리턴한다.
 *
 */
public class DynRun {

     static public Object run(String classNameString, String methodNameString) throws Exception {
        return run(classNameString, methodNameString, null, null, null, null);
        //return run(classNameString, methodNameString, new Class[0], null, new Class[0], null);
    }

    /**
     * 정의한 클래스생성 매소드실행결과를 리턴한다. 각각 지정한 파라메터로 클래스를 생성하고, 매소드를 실행한다.
     * @param classNameString   클래스명
     * @param methodNameString 매소드명
     * @param paramTypesClassConstructor  파라메터의 타입 클래스를 정의 - 클래스생성자 용
     * @param paramsClassConstructor      파라메터를 정의              - 클래스생성자 용
     * @param paramTypesMethod            파라메터의 타입 클래스를 정의 - 매소드 용
     * @param paramsMethod                파라메터를 정의              - 매소드 용
     * @return 매소드 결과를 리턴
     * @throws Exception 예외처리
     */
    static public Object run(String classNameString, String methodNameString
            , Class[]  paramTypesClassConstructor,  Object[] paramsClassConstructor
            , Class[]  paramTypesMethod , Object[] paramsMethod ) throws Exception {

        Class svcClass = Class.forName( classNameString );
        Object svcObject = svcClass.getConstructor( paramTypesClassConstructor ).newInstance( paramsClassConstructor );
        return svcClass.getMethod(methodNameString, paramTypesMethod).invoke(svcObject, paramsMethod);
    }

}
