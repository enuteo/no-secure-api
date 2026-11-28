package bes.tcc.no_roles_api.exception;

public class AccessNotAllowedException extends RuntimeException {
	public AccessNotAllowedException() {
		super("não tem permissão de acessar o recurso");
	}
}
