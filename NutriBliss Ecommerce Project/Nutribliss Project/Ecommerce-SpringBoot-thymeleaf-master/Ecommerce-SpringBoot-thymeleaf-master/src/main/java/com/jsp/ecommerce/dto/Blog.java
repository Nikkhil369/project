package com.jsp.ecommerce.dto;




import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

@Entity
@Data
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] picture;
    private LocalDateTime uploadDateTime;

    public String generateBase64Image() {
        return Base64.encodeBase64String(this.getPicture());
    }
}

