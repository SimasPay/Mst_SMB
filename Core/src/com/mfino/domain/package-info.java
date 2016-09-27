@TypeDefs({
        @TypeDef(
                name="encryptedString",
                typeClass=EncryptedStringType.class,
                parameters = {
                    @Parameter(name="encryptorRegisteredName", value="hibernateStringEncryptor")
              }
        ),
        
        @TypeDef(
                name="uniqueencryptedString",
                typeClass=EncryptedStringType.class,
                parameters = {
                  @Parameter(name="encryptorRegisteredName", value="hibernateUniqueStringEncryptor")
            }
        ),
        
        @TypeDef(
                name="encryptedBigDecimal",
                typeClass=EncryptedBigDecimalAsStringType.class,
                parameters = {
                    @Parameter(name="encryptorRegisteredName", value="hibernateStringEncryptor")
              }
        ),
       
        @TypeDef(
                name="userDefinedTimeStamp",
                typeClass=com.mfino.hibernate.HibernateUTC.TimestampType.class
        )
        
})

package com.mfino.domain;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jasypt.hibernate4.type.EncryptedStringType;
import org.jasypt.hibernate4.type.EncryptedBigDecimalAsStringType;
