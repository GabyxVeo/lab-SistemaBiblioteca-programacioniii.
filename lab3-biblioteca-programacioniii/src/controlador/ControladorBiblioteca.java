package controlador;

import excepciones.RecursoNoDisponibleException;
import excepciones.ReglaNegocioException;
import hilos.OyenteTarea;
import hilos.SimuladorConcurrencia;
import hilos.TareaReporteCatalogo;
import modelo.BibliotecaRepositorio;
import modelo.Libro;
import modelo.RegistradorOperaciones;
import modelo.Revista;
import vista.VentanaBiblioteca;


public class ControladorBiblioteca {

    private final BibliotecaRepositorio repo;
    private final VentanaBiblioteca vista;

    public ControladorBiblioteca(BibliotecaRepositorio repo, VentanaBiblioteca vista) {
        this.repo = repo;
        this.vista = vista;
    }

    public void iniciar() {
        conectarEventos();
        refrescarVista();
        vista.setVisible(true);
    }


    private void conectarEventos() {
        vista.getBtnRegistrar().addActionListener(e -> registrarRecurso());
        vista.getBtnPrestar().addActionListener(e -> prestarRecurso());
        vista.getBtnDevolver().addActionListener(e -> devolverRecurso());
        vista.getBtnMulta().addActionListener(e -> calcularMulta());
        vista.getBtnVerCatalogo().addActionListener(e -> vista.mostrarCatalogo());
        vista.getBtnVerUsuarios().addActionListener(e -> vista.mostrarUsuarios());
        vista.getBtnAgregarUsuario().addActionListener(e -> agregarUsuario());
        vista.getBtnReporte().addActionListener(e -> generarReporte());
        vista.getBtnSimular().addActionListener(e -> simularPrestamoConcurrente());
    }


    private void registrarRecurso() {

        try (RegistradorOperaciones reg = new RegistradorOperaciones("REGISTRO", repo.getBitacora())) {
            String tipo = vista.getTipoSeleccionado();
            String titulo = vista.getTitulo();
            String extra = vista.getDatoExtra();
            reg.registrar("Solicitud de registro: " + tipo + " '" + titulo.trim() + "'");

            String resultado;
            if (tipo.equals("Libro")) {
                resultado = repo.registrar(new Libro(titulo, extra));
            } else {

                resultado = repo.registrar(new Revista(titulo, Integer.parseInt(extra.trim())));
            }

            reg.registrar(resultado);
            reg.marcarExitosa();
            vista.mostrarInformacion("Registro exitoso", resultado);


        } catch (NumberFormatException ex) {
            vista.mostrarError("Error de formato numérico",
                    "La edición de la revista debe ser un número entero (por ejemplo: 45).\n"
                            + "Detalle técnico: " + ex.getMessage());
        } catch (ReglaNegocioException ex) {
            vista.mostrarAdvertencia("Regla de negocio incumplida", ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error inesperado", "No se pudo registrar el recurso: " + ex.getMessage());
        } finally {
            vista.limpiarCampos();
            refrescarVista();
        }
    }



    private void agregarUsuario() {
        String[] datos = vista.pedirDatosUsuario();
        if (datos == null) {
            return;
        }
        try (RegistradorOperaciones reg = new RegistradorOperaciones("USUARIO", repo.getBitacora())) {
            reg.registrar("Solicitud de alta: " + datos[0] + " '" + datos[1].trim() + "' (" + datos[2].trim() + ")");

            String resultado = repo.registrarUsuario(datos[0], datos[1], datos[2], datos[3]);

            reg.registrar(resultado);
            reg.marcarExitosa();
            vista.mostrarInformacion("Usuario registrado", resultado);

        } catch (ReglaNegocioException ex) {
            vista.mostrarAdvertencia("Regla de negocio incumplida", ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error inesperado", "No se pudo registrar el usuario: " + ex.getMessage());
        } finally {
            refrescarVista();
        }
    }



    private void prestarRecurso() {
        try (RegistradorOperaciones reg = new RegistradorOperaciones("PRESTAMO", repo.getBitacora())) {
            String usuario = vista.getUsuario();
            String titulo = vista.getTitulo();
            reg.registrar("Solicitud de préstamo: '" + titulo.trim() + "' para el usuario '" + usuario.trim() + "'");

            String mensaje = repo.procesarPrestamo(usuario, titulo);

            reg.registrar(mensaje);
            reg.marcarExitosa();
            vista.mostrarInformacion("Préstamo exitoso", mensaje);

        } catch (RecursoNoDisponibleException ex) {
            vista.mostrarAdvertencia("Recurso no disponible",
                    ex.getMessage()
                            + "\n\nRecurso: " + ex.getTituloRecurso()
                            + "\nPrestado actualmente a: " + ex.getUsuarioActual());
        } catch (ReglaNegocioException ex) {
            vista.mostrarAdvertencia("Regla de negocio incumplida", ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error inesperado", "No se pudo procesar el préstamo: " + ex.getMessage());
        } finally {
            vista.limpiarCampos();
            refrescarVista();
        }
    }



    private void devolverRecurso() {
        try (RegistradorOperaciones reg = new RegistradorOperaciones("DEVOLUCION", repo.getBitacora())) {
            String usuario = vista.getUsuario();
            String titulo = vista.getTitulo();
            reg.registrar("Solicitud de devolución: '" + titulo.trim() + "' del usuario '" + usuario.trim() + "'");

            String mensaje = repo.devolver(usuario, titulo);

            reg.registrar(mensaje);
            reg.marcarExitosa();
            vista.mostrarInformacion("Devolución exitosa", mensaje);

        } catch (ReglaNegocioException ex) {
            vista.mostrarAdvertencia("Regla de negocio incumplida", ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error inesperado", "No se pudo registrar la devolución: " + ex.getMessage());
        } finally {
            vista.limpiarCampos();
            refrescarVista();
        }
    }



    private void calcularMulta() {
        try (RegistradorOperaciones reg = new RegistradorOperaciones("MULTA", repo.getBitacora())) {
            String titulo = vista.getTitulo();
            int dias = Integer.parseInt(vista.getDiasRetraso().trim());
            reg.registrar("Cálculo de multa: '" + titulo.trim() + "' con " + dias + " días de retraso");

            int multa = repo.calcularMulta(titulo, dias);

            String mensaje = "Multa para '" + titulo.trim() + "' con " + dias + " día(s) de retraso: $" + multa;
            reg.registrar(mensaje);
            reg.marcarExitosa();
            vista.mostrarInformacion("Multa calculada", mensaje);

        } catch (NumberFormatException ex) {
            vista.mostrarError("Error de formato numérico",
                    "Los días de retraso deben ser un número entero (por ejemplo: 3).\n"
                            + "Detalle técnico: " + ex.getMessage());
        } catch (ReglaNegocioException ex) {
            vista.mostrarAdvertencia("Regla de negocio incumplida", ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error inesperado", "No se pudo calcular la multa: " + ex.getMessage());
        } finally {
            vista.limpiarCampos();
            refrescarVista();
        }
    }

    private void generarReporte() {
        vista.setBotonReporteHabilitado(false);
        vista.setProgreso(0);
        vista.setEstado("Generando reporte en segundo plano...");

        TareaReporteCatalogo tarea = new TareaReporteCatalogo(repo, new OyenteTarea() {
            @Override
            public void alProgresar(int porcentaje, String estado) {
                vista.setProgreso(porcentaje);
                vista.setEstado(estado);
                refrescarBitacora();
            }

            @Override
            public void alTerminar(String resumen) {
                vista.setBotonReporteHabilitado(true);
                vista.setEstado("Reporte finalizado.");
                refrescarVista();
                vista.mostrarInformacion("Reporte del catálogo", resumen);
            }
        });

        try {
            new Thread(tarea, "Hilo-Reporte").start();
        } catch (RuntimeException ex) {
            vista.setBotonReporteHabilitado(true);
            vista.mostrarError("Error inesperado", "No se pudo iniciar el reporte: " + ex.getMessage());
        }
    }

    private void simularPrestamoConcurrente() {
        String solicitado = vista.getTitulo().trim();
        try {
            String objetivo = solicitado.isEmpty()
                    ? repo.primerTituloDisponible().orElseThrow(() -> new ReglaNegocioException(
                            "No hay recursos disponibles para la simulación."))
                    : solicitado;

            vista.setBotonSimularHabilitado(false);
            vista.setProgreso(0);
            vista.setEstado("Simulando préstamo concurrente de '" + objetivo + "'...");

            SimuladorConcurrencia simulador = new SimuladorConcurrencia(repo, objetivo,
                    repo.getCarnetUsuario(0), repo.getCarnetUsuario(1), new OyenteTarea() {
                @Override
                public void alProgresar(int porcentaje, String estado) {
                    vista.setProgreso(porcentaje);
                    vista.setEstado(estado);
                    refrescarBitacora();
                }

                @Override
                public void alTerminar(String resumen) {
                    vista.setProgreso(100);
                    vista.setBotonSimularHabilitado(true);
                    vista.setEstado("Simulación finalizada.");
                    refrescarVista();
                    vista.mostrarInformacion("Simulación concurrente", resumen);
                }
            });
            new Thread(simulador, "Hilo-Simulador").start();

        } catch (ReglaNegocioException ex) {
            vista.setBotonSimularHabilitado(true);
            vista.mostrarAdvertencia("Regla de negocio incumplida", ex.getMessage());
        } catch (RuntimeException ex) {
            vista.setBotonSimularHabilitado(true);
            vista.mostrarError("Error inesperado", "No se pudo iniciar la simulación: " + ex.getMessage());
        }
    }


    private void refrescarVista() {
        vista.setCatalogo(String.join("\n", repo.resumenCatalogo()));
        vista.setUsuarios(String.join("\n", repo.resumenUsuarios()));
        refrescarBitacora();
    }

    private void refrescarBitacora() {
        vista.setBitacora(String.join("\n", repo.copiaBitacora()));
    }
}
