package dbinterface;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
import java.sql.*;
import javax.swing.DefaultListModel;

public class DBInterface {

    public DBInterface() {

    }

    //integrado e testado mas pode ser melhorado
    //Check, não altero
    public static synchronized String login(String uname, String pword) {
        Sql sql = new Sql();

        String query1 = "SELECT nomeu, pass FROM utilizadores WHERE nomeu LIKE '" + uname + "'";
        //String query2 = "SELECT nomeu FROM utilizadores WHERE nomeu LIKE '" + uname + "'";
        ResultSet rs = sql.getB(query1);
        try {
            if (!rs.next()) {
                return "unknown_user";
            }
            if (rs.getString("pass").equals(pword)) {
                System.out.println("Login com sucesso(" + rs.getString(1) + ")");
                return "user_found";
            } else if (rs.getString("nomeu").contains(uname)) {
                System.out.println("Palavra passe Errada!(" + rs.getString(1) + ")");
                return "wrong_password";
            } else {
                System.out.println("utilizador inexistente");
                return "unknown_user";
            }

        } catch (Exception e) {
            System.out.println("Erro no login: " + e);
            return "Error in login: " + e;
        }
    }

    //integrado e testado
    //Check Não Altero
    public static synchronized String addUser(String uname, String password) {

        Sql sql = new Sql();
        System.out.println("antes select");
        String query1 = "SELECT nomeu FROM utilizadores WHERE nomeu LIKE '" + uname + "'";

        String query2 = "INSERT INTO utilizadores (nomeu,pass,saldou) VALUES('" + uname + "','" + password + "',100)";

        ResultSet rs = sql.getB(query1);
        try {
            if (rs.next()) {
                String txt = rs.getString(1);
                System.out.print(txt);
                System.out.println(": Já existe");
                return "user_found";
            } else {
                sql.getB(query2);
                System.out.println("Adicionado user com nome:" + uname + "");
                return "accepted_new_user";
            }
        } catch (Exception e) {
            System.out.println("Erro a adicionar user");
            return "Error in addUser: " + e;
        }

    }

    
    //última Versão
    //NÃO INTEGRADO!
    //Faz o quê?
    public synchronized static String depositarSaldo(String user,float valor) throws SQLException{ //check
		Sql sql=new Sql();
		float aux=0;
		String str="";
		ResultSet rs = sql.getB("SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+user+"'");
		try{
			if(rs.next()){
				aux=rs.getInt(1)+valor;
			}
			else{
				str+="Não existe user com esse nome";
			}
			
		}catch(Exception e){
			return "Erro a ler saldo";
		}
		
		PreparedStatement updateSaldo = null;
		String updateStatement = "UPDATE utilizadores SET saldoU= (SELECT saldoU FROM utilizadores WHERE nomeU LIKE'"+user+"')+"+valor+" WHERE nomeU LIKE'"+user+"'";
		try {
	        sql.con.setAutoCommit(false);
	        updateSaldo = sql.con.prepareStatement(updateStatement);
	        updateSaldo.executeUpdate();
	        sql.con.commit();
		 } catch (SQLException e ) {
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "erro";
		            }
		        }
		    } finally {
		        if (updateSaldo != null) {
		            updateSaldo.close();
		        }
		        
		        sql.con.setAutoCommit(true);
		    }
		
		rs = sql.getB("SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+user+"'");
		try{
			if(rs.next()){
				if(rs.getInt(1)==aux)
					str+= "Depositado com sucesso\n";
				else
					str+= "Nao depositou, tente de novo\n";
			}
			
		}catch(Exception e){
			return "Erro a verificar se saldo foi depositado";
		}
		System.out.println(str);
		return str;
	}
    
    //Check Não altero
    //Testado e integrado
    public synchronized String consultarSaldo(String uname) {
        Sql sql = new Sql();
        float saldo = 0;
        String query = "SELECT saldou FROM utilizadores WHERE nomeu LIKE '" + uname + "'";

        ResultSet rs = sql.getB(query);
        try {
            if (rs.next()) {
                saldo = rs.getFloat(1);
                System.out.println("saldo do user " + uname + ":" + saldo + "");
                return Float.toString(saldo);
            }
            return "unknown_user";
        } catch (Exception ex) {
            // error executing SQL statement
            System.out.println("Erro a consultar saldo");
            return "Error in consultarSaldo: " + ex;
        }

    }

    public String recompensasUser(String user) {
        Sql sql = new Sql();

        ResultSet rs = sql.getB("SELECT idR  FROM depositos WHERE nomeUser='" + user + "'AND idR>0");
        String str = "Recompensa:";
        try {
            if (rs.next()) {

                do {

                    ResultSet r = sql.getB("SELECT nomer FROM recompensas WHERE idR=" + rs.getInt(1));
                    str += r.getString(1) + "\n";

                } while (rs.next());
            } else {
                str = "Sem recompensas";
            }
        } catch (Exception ex) {

            return "Erro a ver recompensas de user";
        }
        return str;
    }

    //Coloquei apenas um \n
    public static String mostrarRecompensasProj(int id) {
        Sql sql = new Sql();

        ResultSet rs = sql.getB("SELECT nomer,valor FROM Recompensas WHERE idP=" + id);
        String str = " ";
        try {
            if (rs.next()) {
                do {
                    str += rs.getString(1) + " min pledge: " + rs.getInt(2) + "\n";

                } while (rs.next());
            } else {
                str = "No Rewards to Show";
            }
        } catch (Exception ex) {
            System.out.println("merda");
            return "Erro a ler recompensas de projeto";
        }
        System.out.println(str);
        return str;

    }

    //especial para preencher lista alterado por Manuel
    public DefaultListModel mostrarVotosProjLST(int id) {
        Sql sql = new Sql();
        DefaultListModel listModel = new DefaultListModel();

        ResultSet rs = sql.getB("SELECT nomeV,n FROM Votos WHERE idV=" + id + "AND ativo=" + 1);
        String str = "";
        try {
            if (rs.next()) {
                do {
                    listModel.addElement(rs.getString(1));
                    str += "\n\nNome do voto: " + rs.getString(1) + "\nNumero: " + rs.getInt(2) + "\n";

                } while (rs.next());
            } else {
                listModel.addElement("Este projeto ainda não tem votos ou o projeto já nao se encontra ativo\n");
            }
        } catch (Exception ex) {
            System.out.println("merda");
            return listModel;
        }
        System.out.println(str);
        return listModel;
    }

    //****************fazer o responder a mensagens***********************+++

    public ResultSet listaProjetosActuaisRS() {
        Sql sql = new Sql();
        ResultSet r = sql.getB("SELECT idp as ID, nomep as Name, dono as Owner, finall as Due, pretendido as Wanted, saldop as Ballance FROM Projects WHERE validade=1");
        try { //meter if se tem e else se nao tem?
            while (r.next()) {
                //java.util.Date ini =new java.util.Date(r.getDate(6));
                //java.util.Date fim =new java.util.Date(r.getDate(7));
                //return"\nProjeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"";--> como leio datas para strings?
            }
        } catch (Exception ex) {
            // error executing SQL statement
            //return "Erro a obter lista projetos antigos";
        }
        return r;
    }

    public synchronized ResultSet listaProjetosAntigosRS() {
        Sql sql = new Sql();
        ResultSet r = sql.getB("SELECT idp as ID, nomep as Name, dono as Owner, finall as Due, pretendido as Wanted, saldop as Ballance FROM Projects where validade=0");
        try { //meter if se tem e else se nao tem?
            while (r.next()) {
                //return"\nProjeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"";--> como leio datas para strings?
            }
        } catch (Exception ex) {
            // error executing SQL statement
            System.out.println("Erro a obter lista de projetos antigos");
        }
        return r;
    }

    public synchronized ResultSet listaProjetosTodosRS() {
        Sql sql = new Sql();
        ResultSet r = sql.getB("SELECT idp as ID, nomep as Name, dono as Owner, finall as Due, pretendido as Wanted, saldop as Ballance FROM Projects");
        try { //meter if se tem e else se nao tem?
            while (r.next()) {
                //return"\nProjeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"";--> como leio datas para strings?
            }
        } catch (Exception ex) {
            // error executing SQL statement
            System.out.println("Erro a obter lista de projetos antigos");
        }
        return r;
    }

    public synchronized ResultSet listaProjetosDonoRS(String Dono) {
        Sql sql = new Sql();
        ResultSet r = sql.getB("SELECT idp as ID, nomep as Name, dono as Owner, finall as Due, pretendido as Wanted, saldop as Ballance FROM Projects WHERE dono like '" + Dono + "'");
        try { //meter if se tem e else se nao tem?
            while (r.next()) {
                //return"\nProjeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"";--> como leio datas para strings?
            }
        } catch (Exception ex) {
            // error executing SQL statement
            System.out.println("Erro a obter lista de projetos antigos");
        }
        return r;
    }
    
    public ResultSet openProject(String id) {
        Sql sql = new Sql();
        ResultSet rs = sql.getB("SELECT * FROM projects WHERE idP=" + id);
        return rs;
    }

    //--------------------------------------------
    //          New CODE
    //--------------------------------------------
    
    //***********************FUNÇAO DE VERIFICAR_PRAZOS************************+//
	
	/////////////////////////////ESTA
	public synchronized String updateVAL()  throws SQLException {
		//funciona 
		Sql sql = new Sql();
		PreparedStatement updatedata = null;
		String updateString = "UPDATE Projects SET hoje = SYSDATE";
		
		try {
	        sql.con.setAutoCommit(false);
	        updatedata = sql.con.prepareStatement(updateString);
	        updatedata.executeUpdate();
	        sql.con.commit();
		 } catch (SQLException e ) {
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "erro";
		            }
		        }
		    } finally {
		        if (updatedata != null) {
		        	updatedata.close();
		        }
		        
		        sql.con.setAutoCommit(true);
		    }
		
		String query2 = "SELECT idP FROM Projects";
	
		int numids = 0;
		ResultSet rs = sql.getB(query2);
		try{
			while (rs.next()) {
				++numids;
			}
		}catch(Exception e){
			numids = 0;
		}
		
		PreparedStatement updatevalid = null;

		int i;
		try {
			for (i=1;i<=numids;i++){
				String updateString1 ="UPDATE Projects SET validade = 0 WHERE TRUNC(finall)<=TRUNC(SYSDATE) AND idP = "+i+"";
		        sql.con.setAutoCommit(false);
		        updatevalid = sql.con.prepareStatement(updateString1);
		        updatevalid.executeUpdate();
		        sql.con.commit();
			    }
		}catch (SQLException e ) {
	        
	        if (sql.con != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                sql.con.rollback();
	            } catch(SQLException excep) {
	                return "erro";
	            }
	        }
		} finally {
	        if (updatevalid != null) {
	        	updatevalid.close();
	        }
	        
	        sql.con.setAutoCommit(true);
	    }
		return "actualizados com sucesso";
	}
	
	public synchronized String verificarValidade() throws SQLException{
		
		Sql sql=new Sql();
		updateVAL();
		ResultSet rs=sql.getB("SELECT dono, idP,saldoP,pretendido FROM projects WHERE validade="+0);
		try{
			if(!rs.next())
				return("Nenhum projeto prazou");
			while(rs.next()){
				if(rs.getFloat(2)>=rs.getFloat(3))
					atualizar(rs.getString(1),rs.getInt(2),1); //atigiu saldo
					   
				else
					atualizar(rs.getString(1),rs.getInt(2),0); //nao atingiu saldo
						
				
			}
			
				
			
		}catch(Exception e){
			return "Erro a ver validade de projetos";
		}
		
		return "DB updated";
		
	}
	
//*****************************USERS ********************************************//	

	public synchronized static String depositarSaldoNEW(String user,float valor) throws SQLException{ //check
		Sql sql=new Sql();
		float aux=0;
		String str="";
		ResultSet rs = sql.getB("SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+user+"'");
		try{
			if(rs.next()){
				aux=rs.getInt(1)+valor;
			}
			else{
				str+="Não existe user com esse nome";
			}
			
		}catch(Exception e){
			return "Erro a ler saldo";
		}
		
		PreparedStatement updateSaldo = null;
		String updateStatement = "UPDATE utilizadores SET saldoU= (SELECT saldoU FROM utilizadores WHERE nomeU LIKE'"+user+"')+"+valor+" WHERE nomeU LIKE'"+user+"'";
		try {
	        sql.con.setAutoCommit(false);
	        updateSaldo = sql.con.prepareStatement(updateStatement);
	        updateSaldo.executeUpdate();
	        sql.con.commit();
		 } catch (SQLException e ) {
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "erro";
		            }
		        }
		    } finally {
		        if (updateSaldo != null) {
		            updateSaldo.close();
		        }
		        
		        sql.con.setAutoCommit(true);
		    }
		
		rs = sql.getB("SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+user+"'");
		try{
			if(rs.next()){
				if(rs.getInt(1)==aux)
					str+= "Depositado com sucesso\n";
				else
					str+= "Nao depositou, tente de novo\n";
			}
			
		}catch(Exception e){
			return "Erro a verificar se saldo foi depositado";
		}
		System.out.println(str);
		return str;
	}
	
	public static synchronized void consultarSaldoNEW(String uname){ //check
		Sql sql=new Sql();
		float saldo=0;
         String query =  "SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+uname+"'";
		
		ResultSet rs = sql.getB(query);
		try{
			if(rs.next()){
				saldo= rs.getFloat(1);
				System.out.println("saldo do user "+uname+":"+saldo+"");
		    }
			else{
				System.out.println("Não existe user que está a tentar consultar saldo");
			}
	    }catch (Exception ex){
		      // error executing SQL statement
		      System.out.println("Erro a consultar saldo");
	    }
		
		
		
	}
	//meter actuais e expirados ou tuo junto e ta se(como esta)? 
		public synchronized static String projetosAdmin(String admin){//check
			Sql sql=new Sql();
			String str="";
			str+="\n\nProjetos ativos:\n";
			ResultSet r= sql.getB("SELECT * FROM Projects WHERE dono LIKE '"+admin+"' AND validade="+1);
			try{ //meter if se tem e else se nao tem?
				
				while(r.next()){
					//System.out.println("Projeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"");
					str+="\n\nProjeto "+r.getString(2)+"\n id: "+r.getInt(1)+"\n Descriçao:"+r.getString(4)+"\nAdministador:"+r.getString(3)+"\n saldo objetivo de "+r.getString(6)+"\n saldo atual:"+r.getString(5)+"\n começou em:"+r.getString(7)+" \n acaba em "+r.getString(7)+"\n\n";
				}
				
				
		    }catch (Exception ex){
			      // error executing SQL statement
			      return "Erro a obter projetos que são administrados por user";
		    }
			str+="\n\nProjetos cancelados:\n";
		    r= sql.getB("SELECT * FROM Projects WHERE dono LIKE '"+admin+"' AND validade<"+1);
			try{ //meter if se tem e else se nao tem?
				
				while(r.next()){
					//System.out.println("Projeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"");
					str+="\nProjeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"\n";
				}
				
				
		    }catch (Exception ex){
			      // error executing SQL statement
			      return "Erro a obter projetos que são administrados por user";
		    }
		
			System.out.println(str);
			return str;
		}

                //DA TUDO BEM MAS ENTRA NO ULTIMO CATCH DE ERRO QUANDO USER TEM RECOMPENSAS-->PORQUE?
	  	public synchronized static String recompensasUserNEW(String user){
	  		Sql sql =new Sql();
	  		String str = "";
	  		ResultSet rs = sql.getB("SELECT idRe  FROM depositos WHERE nomeUser='"+user+"'AND idRe>0 AND EntregueR="+0);
	  		str+="\n\n ****Recompensas ainda não validadas pq projetos estão em curso:**\n\n";
	  		try{
	  			if(rs.next()){
	  				
	  				while(rs.next()){
	  					
	  					ResultSet r=sql.getB("SELECT nomeR, idProj FROM Recompensas WHERE idR="+rs.getInt(1));
	  					try{
	  						if(r.next()){
	  							
	  							str += "\nRecompensa:"+r.getString(1)+"\n Projeto com id:"+r.getInt(1)+"\n\n";	
	  						}
	  						
	  					}catch(Exception e){
	  						return "erro";
	  					}
	  				
	  					
	  		    		
	  				}
	  		    }
	  			
	  			
	  			else{
	  		    	str += "\nSem recompensas com projetos em curso\n";
	  		    }
	  			
	  			
	  	    }catch (Exception e){
	  		      
	  		    System.out.println( "Erro a ver recompensas de user"); //erro aqui pq?
	  	    }
	  		
	  		rs = sql.getB("SELECT idRe  FROM depositos WHERE nomeUser='"+user+"'AND idRe>0 AND EntregueR="+1);
	  		str+="\n\n ****Recompensas ja entregues:****\n\n";
	  		try{
	  			if(rs.next()){
	  				
	  				while(rs.next()){
	  					
	  					ResultSet r=sql.getB("SELECT nomeR, idProj FROM Recompensas WHERE idR="+rs.getInt(1));
	  					try{
	  						if(r.next()){
	  							
	  							str += "\nRecompensa:"+r.getString(1)+"\n Projeto com id:"+r.getInt(1)+"\n\n";	
	  						}
	  						
	  					}catch(Exception e){
	  						return "erro";
	  					}
	  				
	  					
	  		    		
	  				}
	  		    }
	  			else{
	  				str += "\nSem recompensas entregues\n";
	  			}
		  	 }catch (Exception e){
		  		      
		  		 System.out.println( "Erro a ver recompensas de user"); //erro aqui pq?
		  	    }
	  		System.out.println(str);
	  		return str;
	  	}
	
//***********************************+PROJETOS************************************//
	
	public synchronized String criarProjeto(String nome, String username, String descricao, String inicio, String fim,float pretendido ){ //check
		Sql sql=new Sql();
		int validade=1;
		ResultSet r= sql.getB("SELECT nomeU FROM utilizadores WHERE nomeU LIKE '"+username+"'");
		try{
			if(r.next()){
				String query = "Select max(idP) from Projects";
				int id=0;
				ResultSet rs= sql.getB(query);
				try{
					if(rs.next())
					   id= rs.getInt(1)+1;
				 
			    }catch (Exception ex){
				      // error executing SQL statement
				      return "Erro a obter id de projeto";
			    }
				try{
				//NOTA:inserir validade a 1--> valido? sem confirmar datas ?
					//sql.setB("INSERT INTO Projects (finall) VALUES (to_date('"+fim+"','dd-mm-yyyy'))" );
					sql.setB("INSERT INTO Projects (idP,nomeP, dono,descricao, saldoP, pretendido,inicio,finall,validade) VALUES ("+id+",'"+nome+"','"+username+"','"+descricao+"',0,"+pretendido+",to_date('"+inicio+"','yy-mm-dd'),"+"to_date('"+fim+"','yy-mm-dd'),"+validade+")");
					//sql.setB("INSERT INTO Projects (idP,nome, admi, saldo, pretendido,inicio,finall,validade) VALUES ("+id+",'"+nome+"','"+username+"',0,"+pretendido+",SYSDATE,to_date('"+inicio+"','dd-mm-yyyy'),SYSDATE,to_date('"+fim+"','dd-mm-yyyy')'" );
					System.out.println("Projeto com id:"+id+"criado");
					return "Projeto com id:"+id+"criado";
				}catch (Exception ex){
					return"Erro a criar projetos";
				}
			
			}
			else
				System.out.println("nao existe nenhum user com esse nome");
				return "nao existe nenhum user com esse nome";
		}catch(Exception e){
			return "Erro";
		}
	}
	
	///////////////////////////////////////esta
	//Quando esta a devolver dinheiro dos users no while so devolve ao primeiro na lista do depositos
    public synchronized String cancelarProjeto(String uname, int proj_id) throws SQLException{
    	Sql sql=new Sql();
		ResultSet rs;
		String user;
		rs=sql.getB("SELECT dono FROM projects WHERE idP="+proj_id);
		try {
			if(rs.next()){
				if(!rs.getString(1).equals(uname)){
					System.out.println("Não é o administrador deste projeto!\n");
					return "Não é o administrador deste projeto!\n";}
			}
		
			else
				return "O projeto que pretende cancelar não existe\n";
		} catch (SQLException e1) {
			return "Ero ao tentar verificar se o projeto que pretende cancelar é seu";
		}
		
		rs = sql.getB("SELECT nomeUser FROM depositos WHERE idPro="+proj_id+"AND entregueR="+0);
		try {
			if(!rs.next()){
				System.out.println("Projeto já não está ativo!!");
			}
		} catch (SQLException e) {
			System.out.println("erro");
			return "Erro a actualizar saldos de users que doaram para o projeto a ser cancelado";
		}
		rs = sql.getB("SELECT nomeUser FROM depositos WHERE idPro="+proj_id);
		try {
			while(rs.next()){
				user=rs.getString(1);
				CallableStatement cs = sql.con.prepareCall("{call REPOR_SALDO ('"+user+"','"+proj_id+"')}");
				cs.execute();
			}
		} catch (SQLException e) {
			System.out.println("erro");
			//return "Erro a actualizar saldos de users que doaram para o projeto a ser cancelado";
		}
		
		//sql.setB("COMMIT"); ///falta este mas não de que  query é que está a fazer commit?
		
		
		PreparedStatement update0 = null;
		PreparedStatement update1 = null;
	    PreparedStatement update2 = null;
	    PreparedStatement update3 = null;
	    PreparedStatement update4 = null;
	    
	    String updateString0 = "DELETE FROM depositos WHERE idPro="+proj_id;
	    String updateString1 ="UPDATE Recompensas SET ativa=0 WHERE idProj="+proj_id;
	    String updateString2 =	"UPDATE Votos SET ativo=0 WHERE idProje="+proj_id;	
	    String updateString3 = "UPDATE projects SET validade = 0 WHERE idP="+proj_id;
	    String updateString4 =	"UPDATE projects SET saldoP ="+0+"WHERE idP="+proj_id;
		
	    try {
	        sql.con.setAutoCommit(false);
	        update0 = sql.con.prepareStatement(updateString0);
	        update1 = sql.con.prepareStatement(updateString1);
	        update2 = sql.con.prepareStatement(updateString2);
	        update3 = sql.con.prepareStatement(updateString3);
	        update4 = sql.con.prepareStatement(updateString4);
	        
	        update0.executeUpdate();
	        update1.executeUpdate();
	        update2.executeUpdate();
	        update3.executeUpdate();
	        update4.executeUpdate();
	        sql.con.commit();
	        
	    } catch (SQLException e ) {
	        if (sql.con != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                sql.con.rollback();
	            } catch(SQLException excep) {
	                return "erro";
	            }
	        }
	    } finally {
	    	
	    	if (update0 != null) {
	            update0.close();
	        }
	    
	        if (update1 != null) {
	            update1.close();
	        }
	        if (update2 != null) {
	            update2.close();
	        }
	        if (update3 != null) {
	            update3.close();
	        }
	        if (update4 != null) {
	            update4.close();
	        }
	       
	        sql.con.setAutoCommit(true);
	       }  
		return "Projecto cancelado com sucesso\n";
   
    }
    
    /////////////////////////////////////esta
    public synchronized static String atualizar(String uname, int proj_id,int cumprir)throws SQLException{
    	Sql sql=new Sql();
		ResultSet rs;
		String user;
		rs=sql.getB("SELECT dono FROM projects WHERE idP="+proj_id);
		try {
			if(rs.next()){
				if(!rs.getString(1).equals(uname))
					return "Não é o administrador deste projeto!\n";
			}
		
			else
				return "O projeto que pretende cancelar não existe\n";
		} catch (SQLException e1) {
			return "Ero ao tentar verificar se o projeto que pretende cancelar é seu";
		}
		
		 
		if(cumprir==1){//atingiu saldo objectivo
			
			PreparedStatement updateAt1 = null;
			PreparedStatement updateAt2 = null;
			PreparedStatement updateAt3 = null;
			 
			String updateStringA1 = "UPDATE depositos SET entregueR="+1+"WHERE idPro="+proj_id;
			String updateStringA2 = "UPDATE Recompensas SET ativa="+0+"WHERE idProj="+proj_id;
			String updateStringA3 = "UPDATE Votos SET ativo=0 WHERE idProje="+proj_id;
			
			try {
			        sql.con.setAutoCommit(false);
			        updateAt1 = sql.con.prepareStatement(updateStringA1);
			        updateAt2 = sql.con.prepareStatement(updateStringA2);
			        updateAt3 = sql.con.prepareStatement(updateStringA3);
			        
			        updateAt1.executeUpdate();
			        updateAt2.executeUpdate();
			        updateAt3.executeUpdate();
			        
			        sql.con.commit();
			        
			 } catch (SQLException e ) {
			        if (sql.con != null) {
			            try {
			                System.err.print("Transaction is being rolled back");
			                sql.con.rollback();
			            } catch(SQLException excep) {
			                return "erro";
			            }
			        }
			    } finally {
			        if (updateAt1 != null) {
			        	updateAt1.close();
			        }
			        if (updateAt2 != null) {
			        	updateAt2.close();
			        }
			        if (updateAt3 != null) {
			        	updateAt3.close();
			        }
			        sql.con.setAutoCommit(true);
			}
		}
		
		else{ //nao atingiu saldo objetivo
			
			
			
			sql.setB("UPDATE depositos SET entregueR=-1 WHERE idPro="+proj_id);
			sql.setB("UPDATE Recompensas SET ativa="+0+"WHERE idProj="+proj_id);
			sql.setB("UPDATE Votos SET ativo=0 WHERE idProje="+proj_id);
			
			
			PreparedStatement updateNAt1 = null;
			PreparedStatement updateNAt2 = null;
			PreparedStatement updateNAt3 = null;
			 
			String updateStringNA1 = "UPDATE depositos SET entregueR=-1 WHERE idPro="+proj_id;
			String updateStringNA2 = "UPDATE Recompensas SET ativa="+0+"WHERE idProj="+proj_id;
			String updateStringNA3 = "UPDATE Votos SET ativo=0 WHERE idProje="+proj_id;
			
			try {
			        sql.con.setAutoCommit(false);
			        updateNAt1 = sql.con.prepareStatement(updateStringNA1);
			        updateNAt2 = sql.con.prepareStatement(updateStringNA2);
			        updateNAt3 = sql.con.prepareStatement(updateStringNA3);
			        
			        updateNAt1.executeUpdate();
			        updateNAt2.executeUpdate();
			        updateNAt3.executeUpdate();
			        
			        sql.con.commit();
			        
			 } catch (SQLException e ) {
			        if (sql.con != null) {
			            try {
			                System.err.print("Transaction is being rolled back");
			                sql.con.rollback();
			            } catch(SQLException excep) {
			                return "erro";
			            }
			        }
			    } finally {
			        if (updateNAt1 != null) {
			        	updateNAt1.close();
			        }
			        if (updateNAt2 != null) {
			        	updateNAt2.close();
			        }
			        if (updateNAt3 != null) {
			        	updateNAt3.close();
			        }
			        sql.con.setAutoCommit(true);
			}

			rs = sql.getB("SELECT nomeUser FROM depositos WHERE idPro="+proj_id);
			try {
			while(rs.next()){
				user=rs.getString(1);
				CallableStatement cs = sql.con.prepareCall("{call REPOR_SALDO ('"+user+"','"+proj_id+"')}");
				cs.execute();
			}
			} catch (SQLException e) {
				System.out.println("erro");
				//return "Erro a actualizar saldos de users que doaram para o projeto a ser cancelado";
			}
			
			

			PreparedStatement updateNAt4 = null;
			String updateStringNA4 = "UPDATE projects SET saldoP ="+0+"WHERE idP="+proj_id;
			try {
		        sql.con.setAutoCommit(false);
		        updateNAt4 = sql.con.prepareStatement(updateStringNA4);
		        updateNAt4.executeUpdate();
		        sql.con.commit();
			} catch (SQLException e ) {
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "erro";
		            }
		        }
		    } finally {
		        if ( updateNAt4 != null) {
		        	updateNAt4.close();
		        }
		        
		        sql.con.setAutoCommit(true);

		}
	
		
		PreparedStatement updateNAt5 = null;
		String updateString5 = "UPDATE projects SET validade = 0 WHERE idP="+proj_id;
		try {
	        sql.con.setAutoCommit(false);
	        updateNAt5 = sql.con.prepareStatement(updateString5);
	        updateNAt5.executeUpdate();
	        sql.con.commit();
		} catch (SQLException e ) {
	        if (sql.con != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                sql.con.rollback();
	            } catch(SQLException excep) {
	               return "erro";
	            }
	        }
	    } finally {
	        if (updateNAt5 != null) {
	        	updateNAt5.close();
	        }
	        
	        sql.con.setAutoCommit(true);
	    	}
		
		}
		
		return "Projecto cancelado com sucesso\n";
	
    }
	public synchronized String adicionarRecompensas(int idP, String nome, float valor){
		
		
		Sql sql=new Sql();
		String str="";
		String query = "Select max(idR) from Recompensas";
		int id=0;
		ResultSet rs=sql.getB("SELECT idP FROM projects WHERE idP="+idP+"AND validade="+1);
		try{
			
		  if(!rs.next()){
			  System.out.println("Este projeto não existe ou já não se encontra ativo");
			  return "Este projeto não existe ou já não se encontra ativo";
		  }
				
		}catch(Exception ex){
			return "Erro ";
		}
		ResultSet r= sql.getB(query);
		try{
			if(r.next())
			   id= r.getInt(1)+1;
		 
	    }catch (Exception ex){
		      // error executing SQL statement
		      return "Erro a obter id de Recompensa";
	    }
		try{
			r=sql.getB("SELECT nomeR FROM Recompensas WHERE idProj="+idP+" AND nomeR LIKE '"+nome+"'");
			try{
				if(r.next()){
					str+="Este projeto já tem esta Recompensa";
				}
				else{
					
				
					sql.setB("INSERT INTO Recompensas (idProj,idR,nomeR,valor,ativa) VALUES ("+idP+","+id+",'"+nome+"',"+valor+","+1+")");
					str+= "Recompensa com id:"+id+"criado";
				}
			}catch(Exception ex){
				return "Erro a ler recompensas";
			}
			
		
		}catch(Exception ex){
			return "Erro";
		}
		
		return str;
	}
	
	
	public synchronized String adicionarVotos(int idP, String nome){
		Sql sql=new Sql();
		int n=0,id=0;
		ResultSet rs;
		rs=sql.getB("SELECT idP FROM projects WHERE idP="+idP+"AND validade="+1);
		try{
			
		  if(!rs.next()){
			  System.out.println("Este projeto não existe ou já não se encontra ativo");
			  return "Este projeto não existe ou já não se encontra ativo";
		  }
				
		}catch(Exception ex){
			return "Erro ";
		}
		
		ResultSet r= sql.getB("SELECT max(idV) FROM votos");
		try{
			if(r.next())
			   id= r.getInt(1)+1;
		 
	    }catch (Exception ex){
		      // error executing SQL statement
		      return "Erro";
	    }
		
		try{
			rs=sql.getB("SELECT nomeV FROM Votos WHERE idProje="+idP+" AND nomeV LIKE '"+nome+"'");
			try{
				if(rs.next()){
					return "Este projeto já tem esta escolha de voto";
				}
				else{
					
				
					sql.setB("INSERT INTO Votos (idV,n,nomeV,idProje,ativo) VALUES ("+id+","+n+",'"+nome+"',"+idP+","+1+")");
					return "Voto com nome:"+nome+"e do projeto com id"+idP+"criado";
				}
			}catch(Exception ex){
				return "Erro a ler recompensas";
			}
			
		
		}catch(Exception ex){
			return "Erro";
		}
	
		
		
		
	}
	
	
	public synchronized static String mostrarRecompensasProjNEW(int id){
		Sql sql = new Sql();
		
		ResultSet rs = sql.getB("SELECT nomeR,valor FROM Recompensas WHERE idProj="+id+"AND ativa="+1);
		String str=" ";
		try{
			if(rs.next()){
				do{
		    		str += "\n\nNome da recompensa: " + rs.getString(1)+"\nValor: "+rs.getInt(2)+"\n";
		    		
				}while(rs.next());
		    }
			else{
		    	str = "Este projeto ainda não tem recompensas ou o projeto já nao se encontra ativo\n";
		    }
	    }catch (Exception ex){
		      System.out.println("merda");
		      return "Erro a ler recompensas de projeto";
	    }
		System.out.println(str);
		return str;
		
	}
	public synchronized static String mostrarVotosProj(int id){
        Sql sql = new Sql();
		
		ResultSet rs = sql.getB("SELECT nomeV,n FROM Votos WHERE idProje="+id+"AND ativo="+1);
		String str="";
		try{
			if(rs.next()){
				do{
		    		str += "\n\nNome do voto: " + rs.getString(1)+"\nNumero: "+rs.getInt(2)+"\n";
		    		
				}while(rs.next());
		    }
			else{
		    	str = "Este projeto ainda não tem votos ou o projeto já nao se encontra ativo\n";
		    }
	    }catch (Exception ex){
		      System.out.println("merda");
		      return "Erro a ler votos de projeto";
	    }
		System.out.println(str);
		return str;
	}
	public synchronized static String listaProjetosActuais(){
		Sql sql = new Sql();
		ResultSet r= sql.getB("SELECT * FROM Projects WHERE validade=1");
		String str="";
		try{ //meter if se tem e else se nao tem?
			while(r.next()){
				
				//java.util.Date ini =new java.util.Date(r.getDate(6));
				//java.util.Date fim =new java.util.Date(r.getDate(7));
				str+="\n\nProjeto "+r.getString(2)+"\n id: "+r.getInt(1)+"\n Descriçao:"+r.getString(4)+"\nAdministador:"+r.getString(3)+"\n saldo objetivo de "+r.getString(6)+"\n saldo atual:"+r.getString(5)+"\n começou em:"+r.getString(7)+" \n acaba em "+r.getString(7)+"\n\n";
			}
	    }catch (Exception ex){
		      // error executing SQL statement
		      return "Erro a obter lista projetos antigos";
	    }
		System.out.println(str);
		return str;
	}
	public synchronized static String listaProjetosAntigos(){
		Sql sql = new Sql();
		ResultSet r= sql.getB("SELECT * FROM Projects WHERE validade=0");
		String str="";
		try{ //meter if se tem e else se nao tem?
			while(r.next()){
				str+="\n\nProjeto "+r.getString(2)+"\n id: "+r.getInt(1)+"\n Descriçao:"+r.getString(4)+"\nAdministador:"+r.getString(3)+"\n saldo objetivo de "+r.getString(5)+"\n saldo atual:"+r.getString(4)+"\n começou em:"+r.getString(6)+" \n acaba em "+r.getString(7)+"\n\n";
			   //return"\nProjeto "+r.getString(2)+", com id: "+r.getString(1)+", saldo objetivo de "+r.getString(5)+" saldo atual:"+r.getString(4)+" que começou em:"+r.getString(6)+" e acaba em "+r.getString(7)+"";--> como leio datas para strings?
			}
	    }catch (Exception ex){
		      // error executing SQL statement
		      return "Erro a obter lista de projetos antigos";
	    }
		System.out.println(str);
		return str;
	}
	public synchronized String listaProjTodos(){
		String str="";
		str+="\n\n*****Projetos Ativos:**********\n\n";
		str+=listaProjetosActuais();
		str+="\n\n****Projetos expirados/cancelados*******\n\n";
		str+=listaProjetosAntigos();
		System.out.println(str);
		return str;
	}
		
	//*****************************************+DOACAO DE USERS A PROJETOS****************************//
		///////////////////////////////////////////////////esta
	//PQ NAO LE TODAS AS RECOMPENSAS?
   public synchronized static String doar(String user,int proj,float doacao,String voto)throws SQLException{
		
		Sql sql= new Sql();
		float saldoU=0;
		int idR=0;	
		String str = "Doação feita com sucesso\n";
			
			
		ResultSet rs = sql.getB("SELECT idP FROM projects WHERE idP="+proj);
		try {
			if(rs.next()){
				try{
			    	ResultSet r=sql.getB("SELECT validade FROM projects WHERE idP="+proj+" AND validade=1");
					 if(!r.next()){
						
						 System.out.println("Projeto escolhido fora de validade ou cancelado\n");
						 return "Projeto escolhido fora de validade ou cancelado\n";
					
				    }
			    }catch(Exception e){
			    	return "Erro";
			    }
				
			   
			}
			
			else{
				System.out.println("Não existe projeto com o id especificado("+proj+")\n");
				return "Não existe projeto com o id especificado("+proj+")\n";
			    
		     }
		} catch (SQLException e) {
			System.out.println("Erro ao verificar projeto para doar");
			return("Erro ao verificar projeto para doar");
		}
		
		rs=sql.getB("SELECT nomeUser FROM depositos WHERE nomeUser LIKE'"+user+"' AND idPro="+proj);
		try{
		    	
				 if(rs.next()){
					 System.out.println("Já doou!\n");
				      return "Já doou!\n";
					 
				 }
					

			    
		    }catch(Exception e){
		    	return "Erro";
		    }
		rs = sql.getB("SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+user+"'");
		
		try{
			if(rs.next())
				saldoU=  rs.getFloat(1);
			else{
				System.out.println("nao existe user com id especificado");
				return "nao existe user com id especificado";}
		}catch (SQLException e){
			System.out.println("Erro ao ler saldo do Utilizador");
		}
		
	    if(saldoU>= doacao){
	    	
	    	
	    	PreparedStatement updatesaldoP = null;
	    	PreparedStatement updatesaldoU = null;
	    	
	    	String updateStringP ="UPDATE projects SET saldoP = (SELECT saldoP FROM projects WHERE idP ="+proj+") + "+doacao+" WHERE idP="+proj;
	    	String updateStringU = "UPDATE utilizadores SET saldoU = (SELECT saldoU FROM utilizadores WHERE nomeU = '"+user+"') - "+doacao+" WHERE nomeU = '"+user+"'";
	    	
	    	 try {
	    	        sql.con.setAutoCommit(false);
	    	        updatesaldoP = sql.con.prepareStatement(updateStringP);
	    	        updatesaldoU = sql.con.prepareStatement(updateStringU);
	    	        updatesaldoP.executeUpdate();
	    	        updatesaldoU.executeUpdate();
	    	        sql.con.commit();
	    	 } catch (SQLException e ) {
	    	        
	    	        if (sql.con != null) {
	    	            try {
	    	                System.err.print("Transaction is being rolled back");
	    	                sql.con.rollback();
	    	            } catch(SQLException excep) {
	    	                return "erro";
	    	            }
	    	        }
	    	    } finally {
	    	        if (updatesaldoP != null) {
	    	        	updatesaldoP.close();
	    	        }
	    	        if (updatesaldoU != null) {
	    	        	updatesaldoU.close();
	    	        }
	    	        sql.con.setAutoCommit(true);
	    	    }
			
			rs=sql.getB("SELECT idR,valor FROM recompensas WHERE idProj="+proj+" AND valor <="+doacao+"AND ativa="+1+" ORDER BY valor ASC");
			try{
				
			   while(rs.next()){
						
				   if(doacao>=rs.getFloat(2)){
					    idR=rs.getInt(1);
					    	
						
					}
			  }
				
			}catch (SQLException e){
				System.out.println("Erro ao ver recompensas");
			}
			
			rs=sql.getB("SELECT nomeV FROM votos WHERE idProje="+proj+"AND nomeV LIKE '"+voto+"'");
			try{
				if(!rs.next()){
					System.out.println("Não há voto com esse nome no projeto");
					return "Não há voto com esse nome no projeto";
				}
			}catch(Exception e){
				return "Erro";
			}
			
			int idDe=0;
				
			rs= sql.getB("SELECT max(idD) FROM depositos");
			try{
				if(rs.next())
				   idDe= rs.getInt(1)+1;
			 
		    }catch (Exception ex){
			      // error executing SQL statement
			      return "Erro a obter id de projeto";
		    }
		   
			
		    
		    PreparedStatement updatevotos = null;
		    PreparedStatement insertdep = null;
		    
		    String insertupdate = "INSERT INTO depositos (idD, nomeUser,idPro,nomeVo,idRe,recebido,entregueR) VALUES("+idDe+",'"+user+"',"+proj+",'"+voto+"',"+idR+","+doacao+","+0+")";
		    String updateString = "UPDATE votos SET n = (SELECT n FROM votos WHERE idProje ="+proj+" AND nomeV='"+voto+"')+"+doacao+" WHERE idProje="+proj+"AND nomeV='"+voto+"'";
		    
		    try {
		        sql.con.setAutoCommit(false);
		        updatevotos = sql.con.prepareStatement(updateString);
		        insertdep = sql.con.prepareStatement(insertupdate);
		        updatevotos.executeUpdate();
		        insertdep.executeUpdate();
		        sql.con.commit();
		        
		    } catch (SQLException e ) {
		       
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "erro";
		            }
		        }
		    } finally {
		        if (updatevotos != null) {
		        	updatevotos.close();
		        }
		        if (insertdep != null) {
		        	insertdep.close();
		        }
		        
		        sql.con.setAutoCommit(true);
		    }
		        
	    }
	    else{
	    	System.out.println("sem saldo suficiente");
			return "Utilizador não tem saldo suficiente para doar\n";}
			
		rs = sql.getB("SELECT idPro,recebido FROM depositos WHERE idPro = "+proj+" AND nomeUser = '"+user+"' AND recebido = "+doacao);
		try{
			if(rs.next()){
				if(rs.getInt(1) > 0 && rs.getFloat(1)>0){
					System.out.println(str);
					return(str);}
			}else
				return("Ocorreu erro na doaçao\n");
		}catch (SQLException e){
			System.out.println("Erro na doação\n");
		}
		System.out.println(str);
		return str;
	}
   
   ///////////////////////////////////////////////esta
    public static synchronized String eliminarDoacao(String user, int idP)throws SQLException{
    	Sql sql= new Sql();
    	ResultSet rs = sql.getB("SELECT nomeUser FROM depositos WHERE idPro="+idP+"AND entregueR="+0);
		try {
			if(!rs.next()){
				System.out.println("Projeto já não está ativo ou não doou a este projeto!!");
			}
		} catch (SQLException e) {
			System.out.println("erro");
			return "Erro";
		}
		
		int idVo;
		
		PreparedStatement updateUsaldo = null;
		PreparedStatement updatePsaldo = null;
		String updateString = "UPDATE utilizadores SET saldoU =(SELECT recebido FROM depositos WHERE idPro=" +idP + " AND nomeUser LIKE'" +
				user+"') +(SELECT saldoU FROM utilizadores WHERE nomeU LIKE '"+user+"') WHERE nomeU LIKE '"+user+"'";
		String updateString2 = "UPDATE projects SET saldoP =(SELECT saldoP FROM projects WHERE idP="+idP+")-(SELECT recebido FROM depositos WHERE idPro=" +idP + " AND nomeUser LIKE'" +
				user+"')  WHERE idP="+idP;	
		
		 try {
		        sql.con.setAutoCommit(false);
		        updateUsaldo = sql.con.prepareStatement(updateString);
		        updatePsaldo = sql.con.prepareStatement(updateString2);
		        updateUsaldo.executeUpdate();
		        updatePsaldo.executeUpdate();
		        sql.con.commit();
		 } catch (SQLException e ) {
		        
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "erro";
		            }
		        }
		    } finally {
		        if (updateUsaldo != null) {
		        	updateUsaldo.close();
		        }
		        if (updatePsaldo != null) {
		        	updatePsaldo.close();
		        }
		        sql.con.setAutoCommit(true);
		    }

		rs = sql.getB("SELECT nomeVo FROM depositos WHERE idPro="+idP+"AND nomeUser LIKE '"+user+"'");
		try {
			while(rs.next()){
				
				ResultSet r = sql.getB("SELECT idV FROM votos WHERE idProje="+idP+"AND nomeV LIKE '"+rs.getString(1)+"'");
				try{
					if(r.next()){
						idVo=r.getInt(1);
						CallableStatement cs = sql.con.prepareCall("{call REPOR_N ("+idVo+","+idP+",'"+user+"')}");
						cs.execute();
					}
				}catch(Exception e){
					return "erro";
				}
				
			}
		} catch (SQLException e) {
			System.out.println("erro");
			//return "Erro a actualizar saldos de users que doaram para o projeto a ser cancelado";
		}
		
		
		PreparedStatement delet = null;
		String updateString3 ="DELETE FROM depositos WHERE idPro="+idP+"AND nomeUser LIKE '"+user+"'";
		
		 try {
		        sql.con.setAutoCommit(false);
		        delet = sql.con.prepareStatement(updateString3);
		        delet.executeUpdate();
		        sql.con.commit();
		 } catch (SQLException e ) {
		        
		        if (sql.con != null) {
		            try {
		                System.err.print("Transaction is being rolled back");
		                sql.con.rollback();
		            } catch(SQLException excep) {
		                return "error";
		            }
		        }
		    } finally {
		        if ( delet!= null) {
		        	delet.close();
		        }
		        
		        sql.con.setAutoCommit(true);
		    }
		
		return "Doação eliminada com sucesso\n";
    }
 
       
  //************************++MENSAGENS********************************//
  	  	
  	//QUANDO METERMOS TEMPO NAS TABELAS DE MENSAGENS METER ORDER BY TIME
  	public synchronized DefaultListModel mostrarMensagensLST(int idPro){
  	DefaultListModel listModel = new DefaultListModel();	
            Sql sql=new Sql();
  		String query = "SELECT idProjet,nomU,idM,mensagem FROM mensagem WHERE idProjet = "+idPro;
  		ResultSet rs = sql.getB(query);
  		String txt = "Mensagens no projecto com o id -"+idPro+"- :\n";
  		try{
  			if(rs.next()){
  				do{
                                    
  				txt += "id utilizador: "+rs.getString(2)+"id mensagem: "+rs.getString(3)+" mensagem: "+rs.getString(4)+"\n";
  				listModel.addElement(rs.getString(3));
                                }while(rs.next());
  			}else{
  				txt = "o projecto "+idPro+" não tem mensagens.\n";
  				System.out.println(txt);
                                listModel.addElement(txt);
  				return listModel;
  			}
  	    }catch (Exception ex){
  		      System.out.println("erro a aceder a mensagens");
  	    }
  		System.out.println(txt);
  		return listModel;
  	}
        
        public synchronized String mostrarMensagen(int idMsg){
            String msg = "";
            Sql sql=new Sql();
            String query = "SELECT idProjet,nomU,idM,mensagem FROM mensagem WHERE idM = "+idMsg;
  		ResultSet rs = sql.getB(query);
            try{
  			if(rs.next()){
                               
  				msg = "id utilizador: "+rs.getString(2)+ "\nmensagem: "+rs.getString(4)+"\n";
                                
  			}else{
  				msg = "Vazio";
  				System.out.println(msg);
                                
  				return msg;
  			}
  	    }catch (Exception ex){
  		      System.out.println("erro a aceder a mensagens");
  	    }
  		System.out.println(msg);
            
            return msg;
            
        }
  	
        public synchronized String mostrarResposta(int idMsg){
            String msg = "";
            Sql sql=new Sql();
            String query = "SELECT idProjet,nomU,idM,Resposta FROM mensagem WHERE idM = "+idMsg;
  		ResultSet rs = sql.getB(query);
            try{
  			if(rs.next()){
                               
  				msg = rs.getString(4);
                                
  			}else{
  				msg = "Vazio";
  				System.out.println(msg);
                                
  				return msg;
  			}
  	    }catch (Exception ex){
  		      System.out.println("erro a aceder a mensagens");
  	    }
  		System.out.println(msg);
            
            return msg;
        }
        
  	/////////////////////////////////////////////////esta
  	public synchronized static String mandarMsg(String user,int idP,String mensagem) throws SQLException{
  		Sql sql=new Sql();
  		ResultSet rs;
          int id=0;
        ResultSet r=sql.getB("SELECT idP FROM projects WHERE idP="+idP);
        try{
        	if(!(r.next())){
        		System.out.println("Nao existe esse projeto");
        		return "Nao existe esse projeto";
        	}
        	
        }catch(Exception e){
        	
        }
        r=sql.getB("SELECT nomeU FROM utilizadores WHERE nomeU='"+user+"'");
        try{
        	if(!r.next()){
        		System.out.println("Nao existe esse user");
        		return "Nao existe esse user";
        	}
        	
        }catch(Exception e){
        	
        }
  		rs = sql.getB("SELECT max(idM) FROM mensagem");
  		try{
  			if(rs.next()){
  				id = rs.getInt(1)+1;
  			}
  		}catch(Exception ex){
  			 // error executing SQL statement
  			return"Erro a ler id max de mensagens";
  		}
  		
  		PreparedStatement updateinsert = null;
  		String updateString = "INSERT INTO mensagem (idProjet,nomU,idM,mensagem) VALUES ("+idP+",'"+user+"',"+id+",'"+mensagem+"')";
  		 try {
  	        sql.con.setAutoCommit(false);
  	        updateinsert = sql.con.prepareStatement(updateString);
  	        updateinsert.executeUpdate();
  	        sql.con.commit();
  		} catch (SQLException e ) {
  	        
  	        if (sql.con != null) {
  	            try {
  	                System.err.print("Transaction is being rolled back");
  	                sql.con.rollback();
  	            } catch(SQLException excep) {
  	                return "erro";
  	            }
  	        }
  	    } finally {
  	        if (updateinsert != null) {
  	        	updateinsert.close();
  	        }
  	       
  	        sql.con.setAutoCommit(true);
  		
  	    }
        rs = sql.getB("SELECT COUNT('"+mensagem+"') FROM mensagem WHERE nomU LIKE '"+user+"' AND idProjet ="+idP);
  		int aux = 0;
  		try{
  			if(rs.next()){
  				aux= rs.getInt(1);
  			}
  		}catch(Exception ex){
  		      // error executing SQL statement
  		      System.out.println("Error: " + ex);
  	    }
  		if(aux>0){
  			System.out.println("msg enviada");
  			return "Messagem enviada\n";}
  		else{
  			System.out.println("nao enviou");
  			return "Não conseguiu mandar mensagem\n";
  			}
  	}
  	public synchronized String responderMensagens(String user,int idP, int idM, String resposta) throws SQLException{
  		Sql sql=new Sql();
		String str = "Respondido!!\n";
	
		ResultSet rs = sql.getB("SELECT idP FROM Projects WHERE idP="+idP);
		try {
			if(!rs.next()){
				System.out.println("Não há nenhum projeto com id."+idP+"\n");
				return "Não há nenhum projeto com id."+idP+"\n";}
			
		} catch (SQLException e) {
			System.out.println("Erro a verificar projeto");
		}
		
		rs = sql.getB( "SELECT dono FROM projects WHERE idP="+idP);
		try {
			if(rs.next()){
				if(rs.getString(1).contains(user)){
					
					
					
					PreparedStatement updatemensage = null;
					String updateString = "UPDATE Mensagem SET resposta = '"+resposta+"' WHERE idM="+idM+" AND idProjet="+idP+"";
					
					try {
				        sql.con.setAutoCommit(false);
				        updatemensage = sql.con.prepareStatement(updateString);
				        updatemensage.executeUpdate();
				        sql.con.commit();
					 } catch (SQLException e ) {
					    
					        if (sql.con != null) {
					            try {
					                System.err.print("Transaction is being rolled back");
					                sql.con.rollback();
					            } catch(SQLException excep) {
					               return "erro";
					            }
					        }
					    } finally {
					        if (updatemensage != null) {
					        	updatemensage.close();
					        }
					        
					        sql.con.setAutoCommit(true);
					    }
					
					
				}else{
					System.out.println("Não é admin de projeto de id: "+idP+"\n");
					return "Não é admin de projeto de id: "+idP+"\n";
				}
			}else
				return "Não é admin";
		} catch (SQLException e) {
			System.out.println("Error sending the answer");
			return "erro";
		}
		
		System.out.println(str);
		return str;
	}
    
}

class Sql {

    Statement stmt = null;
    Connection con = null;

    Sql() {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "bd", "bd");
            //stmt = con.createStatement();

        } catch (java.lang.ClassNotFoundException e) {
            System.out.println("DB: Cannot find driver class");
            System.exit(1);
        } catch (java.sql.SQLException e) {
            System.out.println("DB: Cannot get connection");
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("DB: cannot create statement");
            System.exit(1);
        }

    }

    void setB(String s) {
        System.out.println("Executing " + s);
        try {
            //stmt.executeUpdate(s);
            stmt = con.prepareStatement(s, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(s);
        } catch (SQLException ex) {
            // error executing SQL statement
            System.out.println("Error: " + ex);
        }
    }

    ResultSet getB(String s) {
        System.out.println("Executing " + s);
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(s, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(s);
        } catch (SQLException ex) {
            // error executing SQL statement
            System.out.println("Error: " + ex);
        }
        return rs;
    }

    void closeDatabase() {
        // close database
        try {
            con.close();
        } catch (Exception ex) {
        }
    }
}
