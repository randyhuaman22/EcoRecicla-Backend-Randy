package pe.upeu.ecorecicla.exception;
public class ValidacionNegocioException extends RuntimeException { private final String campo; public ValidacionNegocioException(String campo,String m){super(m);this.campo=campo;} public String getCampo(){return campo;} }
