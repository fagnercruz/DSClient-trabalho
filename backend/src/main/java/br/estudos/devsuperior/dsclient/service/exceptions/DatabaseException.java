package br.estudos.devsuperior.dsclient.service.exceptions;

public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 2804040570812160471L;

	public DatabaseException(String msg) {
		super(msg);
	}

}
