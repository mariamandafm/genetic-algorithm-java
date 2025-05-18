package main.java.benchmark.jmeter;

import main.java.ProcessOrders;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class GESampler extends AbstractJavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();
        result.sampleStart();

        try{
            ProcessOrders.main(new String[0]);

            result.sampleEnd();
            result.setSuccessful(true);
            result.setResponseMessage("Executado");
            result.setResponseCodeOK();
        } catch (Exception e){
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseMessage("Erro: " + e);
            result.setResponseCode("500");
        }

        return result;
    }
}
