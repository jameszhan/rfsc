
package com.mulberry.athena.compile;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 10:33 PM
 */
public class CompiledResult {

    private final boolean success;
    private final String errorMessage;
    private final Class<?> clazz;

    public CompiledResult(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.clazz = null;
    }

    public CompiledResult(Class<?> clazz) {
        this.success = true;
        this.errorMessage = null;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        if (success) {
            return clazz;
        } else {
            return null;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
