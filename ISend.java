
import mpi.*;
import java.nio.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.IntBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import java.util.Arrays;

class test
{
    // ISend numberOfIntToSend int from other proc on targetRank
    public static void main(String[] args) throws MPIException, InterruptedException
    {

        MPI.Init(args);
        int myself = MPI.COMM_WORLD.getRank(); // rank of current mpi instance
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int tag = 40;
        int iterations = 50000;
        Random rand = new Random();
 
        System.out.println("myself " + myself);
        if(myself == 0)
        {
            int dest = 1;
            int capacity = 20;
            
            FloatBuffer in  = MPI.newFloatBuffer(capacity);
            //Request[] requests = new Request[iterations]; // Store requests

            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < capacity; j++) {
                    in.put(rand.nextFloat());
                }
			    Request request = MPI.COMM_WORLD.iSend(in, in.capacity(), MPI.FLOAT, dest, tag);
                in = MPI.newFloatBuffer(capacity); // create a new buffer.

                System.out.println("Envoi terminé !");
            }
           
            //Request.waitAll(requests); // Wait for all sends to complete.
            System.out.println("sending over");
        }else
        { 
            int source = 0;
            
            for (int i = 0; i < iterations; i++) {
                Status st = MPI.COMM_WORLD.probe(source, tag);

                int sizeOfMessage = st.getCount(MPI.FLOAT);
                float[] message = new float[sizeOfMessage];
                MPI.COMM_WORLD.recv(message, sizeOfMessage, MPI.FLOAT, source, tag);

                System.out.println("received " + message[0]);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("receive end");
        }

        MPI.Finalize();
    }
}