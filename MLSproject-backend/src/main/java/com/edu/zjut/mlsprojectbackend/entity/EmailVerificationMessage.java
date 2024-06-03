package com.edu.zjut.mlsprojectbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationMessage {
	private String type;
	private String email;
	private int code;

}