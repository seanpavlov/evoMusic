package com.evoMusic.database;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.DBObject;

/**
 *
 * Use for put into document: document.put(key, DBEmum.of(SomeEnum.VALUE));
 * Use for get from document: DBEnum.to(SomeEnum.class, document.get(key));
 *
 * Credits to GHad
 * source: http://ghads.wordpress.com/2011/04/12/mongodb-and-java-enums/
 */
public class DBEnum<T extends Enum<T>>
        implements DBObject
{
 
    private static final String KEY = "_enum";
 
    public static <U extends Enum<U>> DBEnum<U> of(U value)
    {
        return new DBEnum<U>(value);
    }
 
    public static <U extends Enum<U>> U to(Class<U> c, Object o)
    {
        if (o instanceof DBObject)
        {
            Object value = ((DBObject) o).get(KEY);
            if (value == null)
            {
                return null;
            }
            return Enum.valueOf(c, value.toString());
        }
        return null;
    }
 
    private final T value;
 
    private DBEnum(T value)
    {
        this.value = value;
    }
 
    public void markAsPartialObject() { }
 
    public boolean isPartialObject() { return false; }
 
    public Object put(String s, Object o) { return null; }
 
    public void putAll(BSONObject bsonObject) { }
 
    public void putAll(Map map) { }
 
    public Object get(String s)
    {
        return value.name();
    }
 
    public Map toMap()
    {
        return Collections.singletonMap(KEY, value.name());
    }
 
    public Object removeField(String s) { return null; }
 
    public boolean containsKey(String s)
    {
        return KEY.equals(s);
    }
 
    public boolean containsField(String s)
    {
        return KEY.equals(s);
    }
 
    public Set<String> keySet()
    {
        return Collections.singleton(KEY);
    }
 
    public String toString()
    {
        return "{ \"" + KEY + "\" : \"" + value.name() + "\" }";
    }
}