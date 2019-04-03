package org.miage.isiForm;

/*import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;*/
import org.miage.isiForm.google.sheets.Workbook;

import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;

public class BirtReporter {

    private final static String BIRT_PATH   = "C:\\Users\\alexa\\Documents\\birt-runtime-4.8.0-20180626\\ReportEngine";
    private final static String LOG_PATH    = "output";
    private final static String OUTPUT_PATH = "output\\test.pdf";
    private final static String BIRT_REPORT = "C:\\Users\\alexa\\Documents\\birt-rcp-report-designer-4.8.0-20180626-win32.win32.x86_64\\birt\\workspace\\";

    /*public static void getReportAsPdf(Workbook workbook, String report) {
        try {
            EngineConfig config = new EngineConfig();
            config.setBIRTHome(BIRT_PATH);
            config.setLogConfig(LOG_PATH, Level.FINEST);
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory)Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            IReportEngine        engine  = factory.createReportEngine(config);

            IReportRunnable design = engine.openReportDesign(BIRT_REPORT + report);
            System.out.println(design.getDesignHandle().getClass());
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);

            PDFRenderOption PDF_OPTIONS = new PDFRenderOption();
            PDF_OPTIONS.setOutputFileName(OUTPUT_PATH);
            PDF_OPTIONS.setOutputFormat("pdf");

            task.setRenderOption(PDF_OPTIONS);
            task.run();
            task.close();
            engine.destroy();
        } catch(final Exception ex) {
            ex.printStackTrace();
        } finally {
            Platform.shutdown();
        }
    }

    private static void changeDataSource(ReportDesignHandle designHandle) throws SemanticException {
        SlotHandle datasources = designHandle.getDataSources();
        SlotIterator iter = (SlotIterator) datasources.iterator();
        while (iter.hasNext()) {
            DesignElementHandle dsHandle = (DesignElementHandle) iter.next();
            if (dsHandle instanceof OdaDataSourceHandle && dsHandle.getName().equals("db")) {
                dsHandle.setProperty("prop", "valeur");
            }
        }
    }*/
}
