package foo.models;

import java.util.Map;

/**
 * Represents an object which contains a lot of the associated edge cases
 * that should be handled without error
 */
public class EdgeCaseModel {
    // Common Arrays
    private int[] arrayInt;
    private int[][] arrayArrayInt;
    private Integer[] arrayInteger;
    private Integer[] arrayArrayInteger;

    // Map Types
    private Map rawMap;
    private Map<String, Object> objectMap;
    private Map<String, String> stringMap;

    // Complex Array
    private Map[] arrayRawMap;
    private Map<String, String>[] arrayStringMap;

    // Potentially generated attribute use case
    private EdgeCaseModel _123$$3;

    public int[] getArrayInt() {
        return arrayInt;
    }

    public void setArrayInt(int[] arrayInt) {
        this.arrayInt = arrayInt;
    }

    public int[][] getArrayArrayInt() {
        return arrayArrayInt;
    }

    public void setArrayArrayInt(int[][] arrayArrayInt) {
        this.arrayArrayInt = arrayArrayInt;
    }

    public Integer[] getArrayInteger() {
        return arrayInteger;
    }

    public void setArrayInteger(Integer[] arrayInteger) {
        this.arrayInteger = arrayInteger;
    }

    public Integer[] getArrayArrayInteger() {
        return arrayArrayInteger;
    }

    public void setArrayArrayInteger(Integer[] arrayArrayInteger) {
        this.arrayArrayInteger = arrayArrayInteger;
    }

    public Map getRawMap() {
        return rawMap;
    }

    public void setRawMap(Map rawMap) {
        this.rawMap = rawMap;
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, Object> objectMap) {
        this.objectMap = objectMap;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public Map[] getArrayRawMap() {
        return arrayRawMap;
    }

    public void setArrayRawMap(Map[] arrayRawMap) {
        this.arrayRawMap = arrayRawMap;
    }

    public Map<String, String>[] getArrayStringMap() {
        return arrayStringMap;
    }

    public void setArrayStringMap(Map<String, String>[] arrayStringMap) {
        this.arrayStringMap = arrayStringMap;
    }

    public foo.models.EdgeCaseModel get_123$$3() {
        return _123$$3;
    }

    public void set_123$$3(foo.models.EdgeCaseModel _123$$3) {
        this._123$$3 = _123$$3;
    }


}

