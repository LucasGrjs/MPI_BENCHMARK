import javax.print.attribute.standard.RequestingUserName;

import mpi.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class test
{
    // proc 0 gather MAXLEN int from other proc
    public static void main(String[] args) throws MPIException
    {
        int MAXLEN = 7;
        MPI.Init(args);

        int myself = MPI.COMM_WORLD.getRank();
        int tasks = MPI.COMM_WORLD.getSize();

        int out[] = new int[MAXLEN];    
        int value = myself * MAXLEN;
        for(int i = 0; i < MAXLEN; i++)
        {
            out[i] = i;
        }
        
        for (int i = 0; i < out.length; i++) {
            System.out.println("proc N°" + myself + " [" + i + "] = " + out[i]);
        }

        if(myself == 0) // root gathering data
        {
            int in[] = new int[MAXLEN * tasks];
            MPI.COMM_WORLD.gather(out, MAXLEN, MPI.INT, in, MAXLEN, MPI.INT, 0); // receive
            
            System.out.println("DONE GATHER RECV----------- ");
            
            System.out.println("RESULT : ");
            for (int i = 0; i < in.length; i++) {
                System.out.println("[" + i + "] = " + in[i]);
            }
        }else // others sending data
        {
            MPI.COMM_WORLD.gather(out, MAXLEN, MPI.INT, 0); // send 
            System.out.println("DONE SENDING for N°" + myself);
        }
        
        MPI.Finalize();
    }
}