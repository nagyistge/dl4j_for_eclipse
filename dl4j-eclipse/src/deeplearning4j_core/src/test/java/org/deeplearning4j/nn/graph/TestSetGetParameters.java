package deeplearning4j_core.src.test.java.org.deeplearning4j.nn.graph;

//import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
//import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
//import org.deeplearning4j.nn.conf.layers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.layers.DenseLayer;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.layers.GravesBidirectionalLSTM;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.layers.GravesLSTM;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.layers.OutputLayer;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import deeplearning4j_nn.src.main.java.org.deeplearning4j.nn.graph.ComputationGraph;

public class TestSetGetParameters {

    @Test
    public void testInitWithParamsCG() {

        Nd4j.getRandom().setSeed(12345);

        //Create configuration. Doesn't matter if this doesn't actually work for forward/backward pass here
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .graphBuilder()
                .addInputs("in")
                .addLayer("0", new DenseLayer.Builder().nIn(10).nOut(10).build(),"in")
                .addLayer("1", new GravesLSTM.Builder().nIn(10).nOut(10).build(),"in")
                .addLayer("2", new GravesBidirectionalLSTM.Builder().nIn(10).nOut(10).build(),"in")
                .addLayer("3", new ConvolutionLayer.Builder().nIn(10).nOut(10).kernelSize(2, 2).stride(2, 2).padding(2, 2).build(), "in")
                .addLayer("4", new OutputLayer.Builder(LossFunction.MCXENT).nIn(10).nOut(10).build(),"3")
                .addLayer("5", new OutputLayer.Builder(LossFunction.MCXENT).nIn(10).nOut(10).build(),"0")
                .addLayer("6", new RnnOutputLayer.Builder(LossFunction.MCXENT).nIn(10).nOut(10).build(), "1","2")
                .setOutputs("4","5","6")
                .pretrain(false).backprop(true).build();

        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        INDArray params = net.params();


        ComputationGraph net2 = new ComputationGraph(conf);
        net2.init(params, true);

        ComputationGraph net3 = new ComputationGraph(conf);
        net3.init(params, false);

        assertEquals(params, net2.params());
        assertEquals(params, net3.params());

        assertFalse(params == net2.params());    //Different objects due to clone
        assertTrue(params == net3.params());    //Same object due to clone


        Map<String, INDArray> paramsMap = net.paramTable();
        Map<String, INDArray> paramsMap2 = net2.paramTable();
        Map<String, INDArray> paramsMap3 = net3.paramTable();
        for (String s : paramsMap.keySet()) {
            assertEquals(paramsMap.get(s), paramsMap2.get(s));
            assertEquals(paramsMap.get(s), paramsMap3.get(s));
        }
    }
}