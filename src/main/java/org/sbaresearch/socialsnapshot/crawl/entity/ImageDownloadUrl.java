package org.sbaresearch.socialsnapshot.crawl.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definiert ein in einem {@link FbObject} feld gespeicherten string als URL zu einem Image das heruntergeladen werden soll
 * 
 * @author Maurice Wohlkönig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ImageDownloadUrl {
	
	int value() default 10;
}
