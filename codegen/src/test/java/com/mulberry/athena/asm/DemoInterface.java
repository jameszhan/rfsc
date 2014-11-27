package com.mulberry.athena.asm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebParam.Mode;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 11/19/14
 *         Time: 4:36 PM
 */
@WebService(name="Calculator", serviceName="CalculatorService")
@SOAPBinding(style=Style.DOCUMENT, use= SOAPBinding.Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface DemoInterface extends Serializable {

    @WebResult(name = "addResponse", partName = "methodPart")
    @WebMethod(operationName = "addOperation")
    int add(
            @WebParam(name = "oprand1", mode = Mode.IN, partName = "oprandPart") int a,
            @WebParam(name = "oprand2", mode = Mode.IN, partName = "oprandPart") int b
    );

    String hello(String message);




}
