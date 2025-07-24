package main.java.benchmark.jmeter;

import main.java.ProcessCoordinatesExecutorV1;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class CoordinateProcessingSampler extends AbstractJavaSamplerClient {
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("filePath", "C:\\\\Users\\\\amand\\\\code\\\\GeneticAlgorithm\\\\coordenadas_1000_50.txt");
        return params;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        String filePath = context.getParameter("filePath");

        result.sampleStart(); // ⏱ Inicia a medição do tempo

        try {
            ProcessCoordinatesExecutorV1.calculateBestRoutes(filePath);
            result.sampleEnd(); // ✅ Finaliza a medição do tempo
            result.setSuccessful(true);
            result.setResponseMessage("Processamento concluído com sucesso.");
            result.setResponseCodeOK();
        } catch (InterruptedException e) {
            result.sampleEnd(); // Mesmo com erro, finalize a medição
            result.setSuccessful(false);
            result.setResponseMessage("Erro: " + e.getMessage());
            result.setResponseCode("500");
        }

        return result;
    }
}
