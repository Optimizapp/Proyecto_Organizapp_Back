package co.javeriana.dw.organizapp.entity;

public enum NodeType {
    START_EVENT,
    END_EVENT,
    TASK,
    GATEWAY,
    MESSAGE_THROW,
    MESSAGE_CATCH,
    SUBPROCESS,

    // Valores legacy conservados para no romper datos existentes ya persistidos.
    INICIO,
    TAREA,
    DECISION,
    FIN,
    SUBPROCESO
}
