package com.antgroup.openspgapp.arks.sofaboot;

// import com.antgroup.openspg.cloudext.impl.computingengine.aistudio.AiStudioClientDriver;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspgapp.core.reasoner.service.utils.ReasonerValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pemja.core.PythonInterpreter;

@Component
@Order(0)
/* loaded from: StartupConfig.class */
public class StartupConfig implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(StartupConfig.class);

  @Autowired private ReasonerValue reasonerValue;

  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (event.getApplicationContext().getParent() == null) {
      // exportKnextVariate();
      // new AiStudioClientDriver();
    }
  }

  private void exportKnextVariate() {
    log.info("Export Knext Variate Started variate:" + this.reasonerValue.toString());
    PythonInterpreter pythonInterpreter =
        PemjaUtils.newPythonInterpreter(
            this.reasonerValue.getPythonExec(), this.reasonerValue.getPythonPaths());
    try {
      pythonInterpreter.exec(
          String.format(
              "import os; os.environ[\"KAG_PROJECT_HOST_ADDR\"] = \"%s\"",
              this.reasonerValue.getSchemaUrlHost()));
      log.info("Export Knext Variate Succeed!!!");
    } catch (Exception e) {
      log.error("Export Knext Variate Error", e);
    } finally {
      pythonInterpreter.close();
    }
  }
}
