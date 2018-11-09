/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.serialization.dummy;

import java.util.*;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import org.mini2Dx.core.serialization.annotation.Field;
import org.mini2Dx.core.serialization.annotation.PostDeserialize;
import org.mini2Dx.natives.Os;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Parent object class for testing serialization
 */
public class TestParentObject extends TestSuperObject {
    @Field
    private int intValue;
    @Field
    private boolean booleanValue;
    @Field
    private byte byteValue;
    @Field
    private short shortValue;
    @Field
    private long longValue;
    @Field
    private float floatValue;
    @Field(optional=true)
    private int [] intArrayValue;
    @Field(optional=true)
    private String [] stringArrayValue;
    @Field
    private String stringValue;
    @Field
    private Map<String, Integer> mapValues;
    @Field
    private List<String> listValues;
    @Field
    private TestChildObject childObject;
    @Field
    private TestChildObject [] childObjectArray;
    @Field(optional=true)
    private TestChildObject optionalChildObject;
    @Field
    private List<TestChildObject> children;
    @Field
    private Map<String, TestChildObject> mapObjectValues;
    @Field
    private Os enumValue;
    @Field
    private TestConstuctorArgObject argObject;
    @Field
    private TestInterface interfaceObject;
    @Field
    private List<TestInterface> interfaceObjectList;
	@Field
	private final List<String> finalStringList = new ArrayList<String>();
	@Field
	private final Map<String, String> finalStringMap = new HashMap<String, String>();
	@Field
	private final String [] finalStringArray = new String[5];
	@Field
	private TestAbstractObject abstractObject;
	@Field
	private ObjectMap<String, String> gdxObjectMap;
	@Field
	private Array<String> gdxArray;
	@Field
	private IntArray gdxIntArray;
	@Field
	private IntSet gdxIntSet;
    
    private int ignoredValue;
    private boolean postDeserializeCalled = false;
    
    public TestParentObject() {}
    
    public TestParentObject(int intValue) {
    	this.intValue = intValue;
    }
    
    @PostDeserialize
    public void postDeserialize() {
    	postDeserializeCalled = true;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public byte getByteValue() {
		return byteValue;
	}

	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}

	public short getShortValue() {
        return shortValue;
    }

    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Map<String, Integer> getMapValues() {
        return mapValues;
    }

    public void setMapValues(Map<String, Integer> mapValues) {
        this.mapValues = mapValues;
    }

    public List<String> getListValues() {
        return listValues;
    }

    public void setListValues(List<String> listValues) {
        this.listValues = listValues;
    }

    public int getIgnoredValue() {
        return ignoredValue;
    }

    public void setIgnoredValue(int ignoredValue) {
        this.ignoredValue = ignoredValue;
    }

    public TestChildObject getChildObject() {
        return childObject;
    }

    public void setChildObject(TestChildObject childObject) {
        this.childObject = childObject;
    }

    public TestChildObject[] getChildObjectArray() {
		return childObjectArray;
	}

	public void setChildObjectArray(TestChildObject[] childObjectArray) {
		this.childObjectArray = childObjectArray;
	}

	public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

	public int[] getIntArrayValue() {
		return intArrayValue;
	}

	public void setIntArrayValue(int[] intArrayValue) {
		this.intArrayValue = intArrayValue;
	}

	public String[] getStringArrayValue() {
		return stringArrayValue;
	}

	public void setStringArrayValue(String[] stringArrayValue) {
		this.stringArrayValue = stringArrayValue;
	}

	public TestChildObject getOptionalChildObject() {
		return optionalChildObject;
	}

	public void setOptionalChildObject(TestChildObject optionalChildObject) {
		this.optionalChildObject = optionalChildObject;
	}

	public List<TestChildObject> getChildren() {
		return children;
	}

	public void setChildren(List<TestChildObject> children) {
		this.children = children;
	}

	public Map<String, TestChildObject> getMapObjectValues() {
		return mapObjectValues;
	}

	public void setMapObjectValues(Map<String, TestChildObject> mapObjectValues) {
		this.mapObjectValues = mapObjectValues;
	}

	public Os getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(Os enumValue) {
		this.enumValue = enumValue;
	}

	public TestConstuctorArgObject getArgObject() {
		return argObject;
	}

	public void setArgObject(TestConstuctorArgObject argObject) {
		this.argObject = argObject;
	}

	public TestInterface getInterfaceObject() {
		return interfaceObject;
	}

	public void setInterfaceObject(TestInterface interfaceObject) {
		this.interfaceObject = interfaceObject;
	}

	public List<TestInterface> getInterfaceObjectList() {
		return interfaceObjectList;
	}

	public void setInterfaceObjectList(List<TestInterface> interfaceObjectList) {
		this.interfaceObjectList = interfaceObjectList;
	}

	public List<String> getFinalStringList() {
		return finalStringList;
	}

	public Map<String, String> getFinalStringMap() {
		return finalStringMap;
	}

	public String[] getFinalStringArray() {
		return finalStringArray;
	}

	public TestAbstractObject getAbstractObject() {
		return abstractObject;
	}

	public void setAbstractObject(TestAbstractObject abstractObject) {
		this.abstractObject = abstractObject;
	}

	public ObjectMap<String, String> getGdxObjectMap() {
		return gdxObjectMap;
	}

	public void setGdxObjectMap(ObjectMap<String, String> gdxObjectMap) {
		this.gdxObjectMap = gdxObjectMap;
	}

	public Array<String> getGdxArray() {
		return gdxArray;
	}

	public void setGdxArray(Array<String> gdxArray) {
		this.gdxArray = gdxArray;
	}

	public IntArray getGdxIntArray() {
		return gdxIntArray;
	}

	public void setGdxIntArray(IntArray gdxIntArray) {
		this.gdxIntArray = gdxIntArray;
	}

	public IntSet getGdxIntSet() {
		return gdxIntSet;
	}

	public void setGdxIntSet(IntSet gdxIntSet) {
		this.gdxIntSet = gdxIntSet;
	}

	public boolean isPostDeserializeCalled() {
		return postDeserializeCalled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TestParentObject that = (TestParentObject) o;
		return intValue == that.intValue &&
				booleanValue == that.booleanValue &&
				byteValue == that.byteValue &&
				shortValue == that.shortValue &&
				longValue == that.longValue &&
				Float.compare(that.floatValue, floatValue) == 0 &&
				Arrays.equals(intArrayValue, that.intArrayValue) &&
				Arrays.equals(stringArrayValue, that.stringArrayValue) &&
				Objects.equals(stringValue, that.stringValue) &&
				Objects.equals(mapValues, that.mapValues) &&
				Objects.equals(listValues, that.listValues) &&
				Objects.equals(childObject, that.childObject) &&
				Arrays.equals(childObjectArray, that.childObjectArray) &&
				Objects.equals(optionalChildObject, that.optionalChildObject) &&
				Objects.equals(children, that.children) &&
				Objects.equals(mapObjectValues, that.mapObjectValues) &&
				enumValue == that.enumValue &&
				Objects.equals(argObject, that.argObject) &&
				Objects.equals(interfaceObject, that.interfaceObject) &&
				Objects.equals(interfaceObjectList, that.interfaceObjectList) &&
				Objects.equals(finalStringList, that.finalStringList) &&
				Objects.equals(finalStringMap, that.finalStringMap) &&
				Arrays.equals(finalStringArray, that.finalStringArray) &&
				Objects.equals(abstractObject, that.abstractObject) &&
				Objects.equals(gdxObjectMap, that.gdxObjectMap) &&
				Objects.equals(gdxArray, that.gdxArray) &&
				Objects.equals(gdxIntArray, that.gdxIntArray) &&
				Objects.equals(gdxIntSet, that.gdxIntSet);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(super.hashCode(), intValue, booleanValue, byteValue, shortValue, longValue, floatValue, stringValue, mapValues, listValues, childObject, optionalChildObject, children, mapObjectValues, enumValue, argObject, interfaceObject, interfaceObjectList, finalStringList, finalStringMap, abstractObject, gdxObjectMap, gdxArray, gdxIntArray, gdxIntSet);
		result = 31 * result + Arrays.hashCode(intArrayValue);
		result = 31 * result + Arrays.hashCode(stringArrayValue);
		result = 31 * result + Arrays.hashCode(childObjectArray);
		result = 31 * result + Arrays.hashCode(finalStringArray);
		return result;
	}
}
