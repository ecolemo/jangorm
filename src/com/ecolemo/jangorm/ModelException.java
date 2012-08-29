package com.ecolemo.jangorm;

import java.sql.SQLException;


public class ModelException extends RuntimeException {
	private static final long serialVersionUID = -5109480144390890191L;

	public ModelException(String message) {
		super(message);
	}

	public ModelException(Exception e) {
		super(e);
	}

	public ModelException(String message, SQLException e) {
		super(message, e);
	}

}
