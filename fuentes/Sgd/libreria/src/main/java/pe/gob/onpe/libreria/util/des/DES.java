/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.libreria.util.des;

/**
 *
 * @author ECueva
 */
public class DES {
    /** Creates a new instance of DES */
    public DES() {
        int i;
        for(i=0; i<Ki.length; i++)
            Ki[i] = new byte[49];
    }
    
    /**
     * Encripta una cadena usando el algoritmo DES
     * @param data Cadena a encriptar
     * @param key  Clave a usar en la encriptacion (16 digitos)
     * @return Devuelve la cadena encriptada
     */
    public String cipher(String data, String key)
    {
        return HP_DES( CIPHER, data, key);
    }

    /**
     * Desencripta una cadena usando el algoritmo DES
     * @param data Cadena a desencriptar
     * @param key  Clave a usar en la desencriptacion (16 digitos)
     * @return Devuelve la cadena desencriptada
     */
    public String decipher(String data, String key)
    {
        return HP_DES( DECIPHER, data, key);
    }

    private final static int CIPHER = 1;
    private final static int DECIPHER = 2;
    //  unsigned char IP[64] =
    private final static byte IP[] =
    {
        58,50,42,34,26,18,10,2,
        60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6,
        64,56,48,40,32,24,16,8,
        57,49,41,33,25,17,9,1,
        59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5,
        63,55,47,39,31,23,15,7
    };

    /*******************************************************************************
     Tableau de permutation finale pour les donnees:IPinv[]
    ********************************************************************************/
    private final static byte IPinv[]=
    {
        40,8,48,16,56,24,64,32,
        39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30,
        37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28,
        35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26,
        33,1,41,9,49,17,57,25
    };

    /*******************************************************************************
      Table de la fonction d'expansion: E[]
    ********************************************************************************/
    private final static byte E[]=
    {
        32,1,2,3,4,5,
        4,5,6,7,8,9,
        8,9,10,11,12,13,
        12,13,14,15,16,17,
        16,17,18,19,20,21,
        20,21,22,23,24,25,
        24,25,26,27,28,29,
        28,29,30,31,32,1
    };

    /*******************************************************************************
     Tables des S-BOX
    ********************************************************************************/
    private final static byte S1[]=
    {
        14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,
        0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,
        4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,
        15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13
    };

    private final static byte S2[]=
    {
        15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10,
        3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5,
        0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15,
        13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9
    };

    private final static byte S3[]=
    {
        10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8,
        13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1,
        13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7,
        1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12
    };

    private final static byte S4[]=
    {
        7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15,
        13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9,
        10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4,
        3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14
    };

    private final static byte S5[]=
    {
        2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9,
        14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6,
        4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14,
        11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3
    };

    private final static byte S6[]=
    {
        12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11,
        10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8,
        9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6,
        4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13
    };

    private final static byte S7[]=
    {
        4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1,
        13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6,
        1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2,
        6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12
    };

    private byte S8[]=
    {
        13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7,
        1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2,
        7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8,
        2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11
    };

    /*******************************************************************************
     Tableau de la permutation dans f(R,K): P[]
    *******************************************************************************/
    private final static  byte P[]=
    {
        16,7,20,21,
        29,12,28,17,
        1,15,23,26,
        5,18,31,10,
        2,8,24,14,
        32,27,3,9,
        19,13,30,6,
        22,11,4,25
    };

    /*******************************************************************************
     Tableau de permutation initiale appliquee a la cle de session: PC1[]
    *******************************************************************************/
    private final static  byte PC1[] =
    {
        57,49,41,33,25,17,9,
        1,58,50,42,34,26,18,
        10,2,59,51,43,35,27,
        19,11,3,60,52,44,36,
        63,55,47,39,31,23,15,
        7,62,54,46,38,30,22,
        14,6,61,53,45,37,29,
        21,13,5,28,20,12,4
    };

    /*******************************************************************************
     Tableau de permutation pour le calcul des sous cles : PC2[]
    *******************************************************************************/
    private final static  byte PC2[] =
    {
        14,17,11,24,1,5,
        3,28,15,6,21,10,
        23,19,12,4,26,8,
        16,7,27,20,13,2,
        41,52,31,37,47,55,
        30,40,51,45,33,48,
        44,49,39,56,34,53,
        46,42,50,36,29,32
    };

    /*******************************************************************************
     Tableau des left shifts : LS[]
    *******************************************************************************/
    private final static  byte LS[] =
    {
        0,1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1
    };

    /*******************************************************************************
     Declaration des variables
    *******************************************************************************/
    //byte Ki[17][49];       /* sous cles                           */
    private byte[][] Ki = new byte[17][];

    /****************************************************************************
    INIT_BIT_TAB(dest,source,n):
    Procedure qui d'un tableau de n octets fait un tableau de n*8 bits
    Funcion que de una tabla de n bytes convierte a una tabla de 8*n bits
    *****************************************************************************/
    private void INIT_BIT_TAB(byte  dest[], int nd, byte source[], int n)
    {
        int masque,i,j;

        for(i=0;i<n;i++)
        {
            //masque=0x00000080;
            masque=0x80;
            for(j=0;j<8;j++)
            {
                dest[8*i+j+nd] = (byte) ((source[i] & masque) >> (7-j));
                masque >>= 1;
                //masque /= 2;
            }
        }
}

    /****************************************************************************
     BIN_TO_HEX()   :
    range la valeur hexa sur 8 octets d'un nombre binaire de 64 bits
    Funcion que convierte 8 bytes de una cadena hexad. a 1 bytes decimal
    *****************************************************************************/
    private void BIN_TO_HEX(byte vect[], byte  source[], int ns)
    {
        byte i,j,masque;

        // _fmemset(vect,0,8);
        //memset(vect,0,8);
        for(i=0; i <8; i++)
        {
            masque=7;
            vect[i]=0;
            for(j=0; j<8; j++)
            {
                vect[i] += (byte) ((PUISSANCE(masque)) * source[i*8+j+ns]);
                --masque;
            }
        }
    }

    /****************************************************************************
     puissance():
     calcule 2 … la puissance 'puissance'
     Calcula la potencia de 2 elevado a la n  (2 ^ n)
    *****************************************************************************/
    private byte  PUISSANCE (byte puissance)
    {
        byte i;
        byte res = 1;

        for(i=1 ; i <= puissance; i++)
            res *= 2;

        return(res);
    }

    /****************************************************************************
    VECT_PERMUTATION(vect,n_vect,regle,n_regle):
    Procedure de permutation d'un vecteur  selon une regle indiquee par le
    tableau regle[]:
       - n_vect est le nombre d'elements du vecteur avant la permutatione la
         regle,
       - n_regle est le nombre d'elements de la regle , et donc du vecteur
         resultat.

      - Funcion que permuta los valores de una cadena a posiciones diferentes
        en la misma cadena.

    *****************************************************************************/
    private void  VECT_PERMUTATION(byte[] vect, int nv, int n_vect, byte[]  regle, int n_regle)
    {
        byte[] buff = new byte[vect.length-nv];
        int i;

        // _fmemset(buff,0,65);
        //memset(buff,0,65);
        // _fmemcpy(buff,vect,n_vect);
        //memcpy(buff,vect,n_vect);
        for(i=0;i<vect.length-nv;i++)
            buff[i] = vect[i+nv];

        for(i=0;i<n_regle;i++)
            vect[i+nv] = buff[regle[i]-1];
    }

    /****************************************************************************
    S_BOX_CALC(vect):
    Procedure qui d'un vecteur de 48 bits renvoie un vecteur de 32 bits apres
    passage dans les S_BOX.
    *****************************************************************************/
    private void  S_BOX_CALC (byte  vect[], int nv)
    {
        byte  S_Box[][]=new byte[8][];
        int lig,col,i;

        S_Box[0]=S1;
        S_Box[1]=S2;
        S_Box[2]=S3;
        S_Box[3]=S4;
        S_Box[4]=S5;
        S_Box[5]=S6;
        S_Box[6]=S7;
        S_Box[7]=S8;

        for(i=0;i<8;i++)
        {
            col= 8*vect[1+6*i+nv] + 4*vect[2+6*i+nv] + 2*vect[3+6*i+nv] + vect[4+6*i+nv];
            lig= 2*vect[6*i+nv] + vect[5+6*i+nv];
            //INIT_4BIT_TAB(&vect[4*i],&S_Box[i][col+lig*16]);
            INIT_4BIT_TAB(vect, 4*i+nv, S_Box[i], col+lig*16);
        }
    }

    /****************************************************************************
    INIT_4BIT_TAB(dest,source):
    Procedure qui range la valeur binaire d'un nombre < 0xF dans un tableau dest.
    *****************************************************************************/
    private void  INIT_4BIT_TAB(byte dest[], int nd, byte source[], int ns)
    {
        byte masque,i;
        masque=0x08;
        for(i=0; i<4; i++)
        {
            dest[i+nd] = (byte) ((source[0+ns] & masque)>>(3-i));
            masque >>= 1;
        }
    }

    /****************************************************************************
    XOR(vect1, vect2, nbre_bit):
    Procedure de XOR entre deux vecteurs de (nbre_bit) bits
    Funcion que ejecuta la operacion XOR con cada byte de cada cadena
    *****************************************************************************/
    private void  XOR(byte vect1[], int nv1, byte vect2[], int nv2, int nbre_bit)
    {
        byte i;
        for(i=0; i<nbre_bit; i++)
            vect1[i+nv1] ^= vect2[i+nv2];
    }

    /****************************************************************************
    LEFT_SHIFTS(vecteur,n):
    Procedure de n Left shift(s) d'un vecteur       de taille 56 bits
    *****************************************************************************/
    private  void  LEFT_SHIFTS(byte vect[], int nv, byte n)
    {
        byte i,sauve28,sauve0;
        for(i=0; i<n; i++)
        {
            sauve0 = vect[0+nv];
            // _fmemcpy(vect,&vect[1],27);
            memcpy(vect,0+nv,vect,1+nv,27);
            vect[27+nv] = sauve0;

            sauve28 = vect[28+nv];
            //_fmemcpy(&vect[28],&vect[29],27);
            memcpy(vect,28+nv,vect,29+nv,27);
            vect[55+nv] = sauve28;
        }
    }

    /****************************************************************************
    Calcul_sous_cles():
    calcul des sous cles
    *****************************************************************************/
     private void  CALCUL_SOUS_CLES (byte  K[])
    {
        byte i;
        byte Kb[]= new byte[65];
        byte inter_key[]= new byte[57];

        INIT_BIT_TAB(Kb, 1,K,8);
        VECT_PERMUTATION(Kb,1,64,PC1,56);

        for(i=1; i<=16; i++)
        {
            LEFT_SHIFTS(Kb,1,LS[i]);
            // _fmemcpy(&inter_key[1],&Kb[1],56);
            memcpy(inter_key,1,Kb,1,56);
            VECT_PERMUTATION(inter_key,1,56,PC2,48);
            //   _fmemcpy(&Ki[i][1],&inter_key[1],48);
            memcpy(Ki[i],1,inter_key,1,48);
        }
    }

    /****************************************************************************
    memcpy():
    remplaza la funcion de "C"
    *****************************************************************************/
     private void  memcpy (byte  dest[], int nd, byte source[], int ns, int len)
    {
        byte i;

        for(i=0; i<len; i++)
        {
          dest[nd+i] = source[ns+i];
        }
    }

    /****************************************************************************
    FUNCTION_DES() :
    procedure d'appel pour un calcul DES
    *****************************************************************************/
    private void  FUNCTION_DES(int Flag, byte Data_O[], byte K[], byte Des_res[])
    {
        byte right32_bit[] = new byte[32];
        byte i;
        byte Data_B[]= new byte[81];

        //INIT_BIT_TAB(&Data_B[1],Data_O,8);
        INIT_BIT_TAB(Data_B, 1,Data_O,8);
        //VECT_PERMUTATION(&Data_B[1],64,IP,64);
        VECT_PERMUTATION(Data_B, 1,64,IP,64);

        CALCUL_SOUS_CLES(K);

        /******************* boucle principale de 15 iterations */
        for(i=1; i<=15; i++)
        {
            //  _fmemcpy(right32_bit,&Data_B[33],32);
            memcpy(right32_bit,0,Data_B,33,32);
            //VECT_PERMUTATION(&Data_B[33],32,E,48);
            VECT_PERMUTATION(Data_B, 33, 32, E, 48);
            switch(Flag)
            {
                case CIPHER:
                     XOR(Data_B,33,Ki[i],1,48);
                     break;
                case DECIPHER:
                     XOR(Data_B,33,Ki[17-i],1,48);
                     break;
            }

            S_BOX_CALC(Data_B,33);
            //VECT_PERMUTATION(&Data_B[33],32,P,32);
            VECT_PERMUTATION(Data_B,33,32,P,32);
            XOR(Data_B,33,Data_B,1,32);
            // _fmemcpy(&Data_B[1],right32_bit,32);
            memcpy(Data_B,1,right32_bit,0,32);

        }

        /******************************** 16ieme iteration *****/

        // _fmemcpy(right32_bit,&Data_B[33],32);
        memcpy(right32_bit,0,Data_B,33,32);
        //VECT_PERMUTATION(&Data_B[33],32,E,48);
        VECT_PERMUTATION(Data_B,33,32,E,48);

        if(Flag==CIPHER)
                       XOR(Data_B,33,Ki[16],1,48);
        else
                       XOR(Data_B,33,Ki[1],1,48);

        S_BOX_CALC(Data_B,33);
        //VECT_PERMUTATION(&Data_B[33],32,P,32);
        VECT_PERMUTATION(Data_B,33,32,P,32);
        XOR(Data_B,1,Data_B,33,32);
        // _fmemcpy(&Data_B[33],right32_bit,32);
        memcpy(Data_B,33,right32_bit,0,32);
        //VECT_PERMUTATION(&Data_B[1],64,IPinv,64);
        VECT_PERMUTATION(Data_B,1,64,IPinv,64);

        /* le resultat est range dans Des_res */
        BIN_TO_HEX(Des_res,Data_B,1);

    }

    /**************************************************************************
        HEX_BIN :     Funcion que convierte un numero ASCII a un
                       valor binario ('F' -- >  0F)
    **************************************************************************/

    private byte HEX_BIN(char c)
    {

        if (c>='0' && c<='9')   return((byte)(c - '0'));
            else                return((byte)(c - 'A' + 10));

    }

    /****************************************************************************
        TO_BCD :  Funcion que empaqueta dos numeros ASCII en binario
                  ( '2B'  --->  [2B]  )
    ****************************************************************************/

    private byte TO_BCD(char ch_1, char ch_2)
    {
        byte b1, b2;
        b1 = HEX_BIN(ch_1);
        b2 = HEX_BIN(ch_2);

        return( (byte)((b1<<4) | b2));
    }

    /****************************************************************************
         DESEMPAQ :    Funcion que desempaqueta una cadena binario a
                       una cadena ASCII.
    ****************************************************************************/

    private String DESEMPAQ(byte[] Block)
    {
        int i;
        int c,n;
        String Buff = new String();

        for(i = 0 ; i < Block.length ; i++)
        {
            if(Block[i]<0) 
               n = Block[i]+256;
            else
                n = Block[i];
            c = (n/16);
            //if(c<0) c+=15;
            Buff += Integer.toHexString(c);
            c = (n%16);
            //if(c<=0) c+=16;
            Buff += Integer.toHexString(c);
           //Buff = Buff + Integer.toHexString(Block[i]);
           //sprintf(Buff+(2*i),"%.2X",Block[i]);
        }
        return Buff;
    }

    /*************************************************************************
        SHOW_EMPAQ :   Funcion que permite mostrar una cadena binaria.
                       Solo usada para efectos de desarrollo.
    *************************************************************************/
    private void SHOW_EMPAQ(String Title, byte[] Block, int n)
    {
       int i;
       String buffer1, buffer2;

       buffer1 = DESEMPAQ(Block);
       buffer2 = buffer1.substring(0,(n*2));
       buffer1 = Title + buffer2.toUpperCase();
       //System.out.println(buffer1);
    }

    /****************************************************************************
        EMPAQ :   Funcion que empaqueta una cadena ASCII en una cadena
                  binaria.
    ****************************************************************************/

    private byte[] EMPAQ( String Buff)
    {
        int i;
        byte[] Block = new byte[Buff.length()/2]; 
        for(i = 0; i < Buff.length(); i +=2)
        {
           //System.out.println("i: " + i );  
           Block[i/2] = TO_BCD(Buff.charAt(i),Buff.charAt(i+1));
        }
        //System.out.println("Block.length: " + Block.length ); 
        return Block;
    }

    /****************************************************************************
        HP_DES :
    ****************************************************************************/

    private String HP_DES(int tipo, String Data_in, String Key)
    {
       //System.out.println("Data_in.length() INICIO : " + Data_in.length());
       byte Data_in_emp[];
       byte Key_emp[];
       byte Data_out_emp[] = new byte[8];
       String data="";
       int rtn = 0;
       try
       {
          if(Data_in.length()<16) while(Data_in.length()<16) Data_in = Data_in.concat(" ");
          
            /* Empaquetar Data_in y Key */
          Data_in_emp = EMPAQ(Data_in);               /* 16 -> 8 */
            
          //SHOW_EMPAQ("Data_in        : ",Data_in_emp,8);
          Key_emp = EMPAQ(Key);                       /* 16 -> 8 */

          // SHOW_EMPAQ("Key            : ",Key_emp,8);
          if (tipo == CIPHER)
              FUNCTION_DES(CIPHER,Data_in_emp,Key_emp ,Data_out_emp);    /* (b) */

          if (tipo == DECIPHER)
              FUNCTION_DES(DECIPHER,Data_in_emp,Key_emp ,Data_out_emp);/* (b) */

          //SHOW_EMPAQ("Data_out_emp   : ",Data_out_emp,8);
          return DESEMPAQ(Data_out_emp); /* 8 -> 16 */
       }
       catch(Exception e)
       {
            System.out.println("ERROR: HP_DES "+ e.getMessage());
            return "";
       }
    }
    
}
