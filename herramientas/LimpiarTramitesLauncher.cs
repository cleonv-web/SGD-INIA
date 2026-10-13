// Un punto de entrada para el operador: doble clic, sin argumentos ni consola previa.
using System;
using System.Diagnostics;
using System.IO;
using System.Text;

internal static class LimpiarTramitesLauncher
{
    private static int Main(string[] args)
    {
        Console.OutputEncoding = Encoding.UTF8;
        string root = AppDomain.CurrentDomain.BaseDirectory;
        if (args.Length != 0) return Fallo("Ejecutar este programa con doble clic, sin parametros.");
        string python = Path.Combine(root, "herramientas", "python-windows", "python.exe");
        string script = Path.Combine(root, "limpiar_tramites.py");
        if (!File.Exists(python) || !File.Exists(script))
            return Fallo("El ejecutable debe permanecer en la carpeta de instalacion del SGD.");
        Console.WriteLine("LIMPIEZA DE TRAMITES DEL PILOTO\n");
        Console.WriteLine("Todas las areas y todos los anos. Se conservan usuarios, claves, permisos y plantillas.");
        Console.WriteLine("Cierre automatico, respaldo, limpieza y reapertura del sistema.");
        try
        {
            var info = new ProcessStartInfo(python, "\"" + script + "\" --simple");
            info.WorkingDirectory = root;
            info.UseShellExecute = false;
            info.EnvironmentVariables["PYTHONUTF8"] = "1";
            using (var process = Process.Start(info))
            {
                process.WaitForExit();
                if (process.ExitCode != 0)
                    return Fallo("El proceso encontro un error. Revisar el mensaje anterior antes de repetirlo.");
            }
            Console.WriteLine("\nLimpieza completada. Abriendo el SGD...");
            // La URL proviene de la configuracion local, validada por el utilitario.
            // Python abre el portal una vez comprobado el reinicio.
            return 0;
        }
        catch (Exception exc) { return Fallo(exc.Message); }
    }

    private static int Fallo(string message)
    {
        Console.Error.WriteLine("\nERROR: " + message);
        Console.WriteLine("Presione una tecla para cerrar esta ventana.");
        if (!Console.IsInputRedirected) Console.ReadKey(true);
        return 1;
    }
}
