package io.kt.cloud.tanda.event;

import java.time.LocalDateTime;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookPlaced {

	public static final String ONLY_ME = "headers['type']=='BookPlaced'";

	private String type;
	
	@Id
	private Long bookId;
	private String customerInfo;
	private String departmentLoc;
	private String arrivalLoc;
	private String bookStatus;
	private String TaxiInfo;
    
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastModifyTime;

	public BookPlaced() {
		this.type = BookPlaced.class.getSimpleName();
	}
}
