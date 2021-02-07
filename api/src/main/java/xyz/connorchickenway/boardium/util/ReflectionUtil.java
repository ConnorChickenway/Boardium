package xyz.connorchickenway.boardium.util;

import java.lang.reflect.Field;
import java.util.Collection;

public class ReflectionUtil {

    public static void setField( Object packet , String field , Object value ) {
        try {
            Field f = packet.getClass().getDeclaredField( field );
            f.setAccessible( true );
            f.set( packet , value );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public static void addObject( Object packet , String field , Object value ) {
        try {
            Field f = packet.getClass().getDeclaredField( field );
            f.setAccessible( true );
            Object object = f.get( packet );
            if ( object instanceof Collection ) {
                ( ( Collection ) object ).add( value );
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
