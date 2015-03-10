
#include <stdio.h>
#include <string.h> //strlen
#include <stdlib.h> //strlen
#include <sys/socket.h>
#include <arpa/inet.h> //inet_addr
#include <unistd.h> //write
#include <pthread.h> //for threading , link with lpthread
#include <fcntl.h>
//the thread function
pthread_mutex_t lock;

struct node
{
	int sock;
	char nama[100];
	struct node *next;
};

struct node *head, *tail;
int countuser=0;

void init()
{
    head=(struct node *)malloc(sizeof(*head));
    tail=(struct node *)malloc (sizeof(*tail));
    head->next=tail;
    tail->next=tail;
}


struct node* append(int nilai, char nama[])
{
    struct node *ptr;
    struct node *t;
    ptr=head;
    while (ptr->next!=tail ) ptr=ptr->next;

    t=(struct node *)malloc(sizeof(*t));
    t->sock=nilai;

    strcpy(t->nama,nama);
    t->next=tail;
    ptr->next=t;
    countuser=countuser+1;
    return ptr;
}

void delete(struct node *ptr)
{
    struct node *t;
    t=ptr->next;
    ptr->next=ptr->next->next;
    countuser=countuser-1;
    free(t);

}

struct node * getnode(char nama[])
{
	struct node *ptr;
	ptr=head;

	while(ptr->next!=tail)
	{
		ptr=ptr->next;
		if (strcmp(ptr->nama,nama)==0)
		{
			printf("ptrnama: %s\n", ptr->nama);
			// break;
			return ptr;
		}
	}
	return 0;
}

void cetak()
{
	struct node *ptr;
	printf("username:  ");
	ptr=head;
	while (ptr != tail) {
	printf("%s\n",ptr->nama);
	ptr = ptr->next;
	};
}

void *connection_handler(void *);


int main(int argc , char *argv[])
{
	int socket_desc , client_sock , c;
	struct sockaddr_in server , client;
	//Create socket
	socket_desc = socket(AF_INET , SOCK_STREAM , 0);
	if (socket_desc == -1)
	{
		printf("Could not create socket");
	}
	puts("Socket created");

	//Prepare the sockaddr_in structure
	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port	 = htons( 8888);
	
	//Bind
	if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
	{
	//print the error message
	perror("bind failed. Error");
	return 1;
	}
	
	puts("bind done");
	//Listen
	listen(socket_desc , 3);
	
	//Accept and incoming connection
	puts("Waiting for incoming connections...");
	c = sizeof(struct sockaddr_in);
	//Accept and incoming connection
	// puts("Waiting for incoming connections...");
	// c = sizeof(struct sockaddr_in);
	
	pthread_t thread_id;
	init();


	while( (client_sock = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c)) )
	{
		printf("%d",socket_desc);
		puts("Connection accepted");
		
		if( pthread_create( &thread_id , NULL , connection_handler , (void*) &client_sock) < 0)
		{
		perror("could not create thread");
		return 1;
		}
		//Now join the thread , so that we dont terminate before the thread
		//pthread_join( thread_id , NULL);
	}

	if (client_sock < 0)
	{
		perror("accept failed");
		return 1;
	}

	return 0;
}
/*
* This will handle connection for each client
* */

void readMsg(int sock, char msg[]){
	int retval;
	char buf[2];

	bzero(&msg, sizeof(msg));
	while ((retval=read(sock, buf, sizeof(buf)-1)) > 0)
		{
			buf[retval]='\0';
			if (buf[0]=='\r'){
				retval=read(sock, buf, sizeof(buf)-1);
				break;
			}	

			sprintf(msg, "%s%s", msg, buf);
		}
}

void sendOnUser(void *socket_desc){
	int sock = *(int*)socket_desc;
	char onuser[1024], msg[1024];
	struct node *ptr;
	ptr=head;

	sprintf(msg, "ONUSER ");
	bzero(&onuser, sizeof(onuser));

	if(countuser==1){
		sprintf(msg, "%s%s\r\n", msg, ptr->next->nama);
		strcpy(onuser, ptr->next->nama);
		write(sock, msg, strlen(msg));
	}

	else if (countuser > 1){
		int i;
		for (i = 1; i <= countuser; ++i)
		{
			ptr=ptr->next;
			if (i==1){
				sprintf(onuser, "%s%s", onuser, ptr->nama);
				// strcpy(onuser, ptr->next->nama);
			}

			else{
				sprintf(onuser, "%s,%s", onuser, ptr->nama);
			}

		}
		sprintf(msg, "%s%s\r\n", msg, onuser);
		write(sock, msg, strlen(msg));
	}
	// sprintf(onuser, "%s%s", onuser, ptr->nama);
	// while (ptr != tail) {
	// 	ptr = ptr->next;
	// 	sprintf(onuser, "%s,%s", onuser, ptr->nama);
	// };

	// printf("%s\n", onuser);
}

void *connection_handler(void *socket_desc)
{
	//Get the socket descriptor
	char buf[2], perv;
	int flag=0, retval;
	struct node * ptr;
	ptr= (struct node *) malloc(sizeof(*ptr));
	
	int sock = *(int*)socket_desc;
	int read_size, session=0;
	char *cmd, *detail, userdest[5];
	char msg[1024], *temp, client_message[1024];
	
	
	write(sock , "Welcome, random citizen!\r\n" , strlen("Welcome, random citizen!\r\n"));

	while (session==0){
		// readMsg(sock, msg);
		bzero(&msg, sizeof(msg));
		while ((retval=read(sock, buf, sizeof(buf)-1)) > 0)
		{
			buf[retval]='\0';
			if (buf[0]=='\r'){
				retval=read(sock, buf, sizeof(buf)-1);
				break;
			}	

			sprintf(msg, "%s%s", msg, buf);
		}

		
		cmd = strtok(msg, " ");
		detail = strtok(NULL, " ");

		if((strcmp(cmd, "USER"))==0){
			append(sock, detail);
			session=1;
		}
	}

	sendOnUser(socket_desc);


	while(1){

	}
	/*
	while ((retval=read(sock, buf, sizeof(buf)-1)) > 0)
		{
			buf[retval]='\0';
			if (buf[0]=='\r'){
				retval=read(sock, buf, sizeof(buf)-1);
				break;
			}	

			sprintf(name, "%s%s", name, buf);
		}
	// read_size=recv(sock, name,1024, 0);
	
	printf("nama: %s: panjang %d\n", name, read_size);

	append(sock, name);
	
	cetak();
	message = "Greetings! I am your connection handler\r\n";
	write(sock , message , strlen(message));
	

	//Receive a message from client
	while(1)
	{
		bzero(&client_message, sizeof(client_message));
		while ((retval=read(sock, buf, sizeof(buf)-1)) > 0)
		{
			buf[retval]='\0';
			if (perv=='\r' && buf[0]=='\n')
				break;
			perv=buf[0];
			sprintf(client_message, "%s%s", client_message, buf);
		}

		//end of string marker

		
		printf("destinasi: %s string %s\n", dest, client_message);
		dest=strtok(client_message," ");
		temp=strtok(NULL," ");
		printf("destinasi: %s string %s\n", dest, client_message);

		//Send the message back to client
		ptr=getnode(dest);
		printf("isi pesan string %s\n untuk %s  dengan sock %d", temp,ptr->nama, ptr->sock);

		//Send the message back to client
		write(ptr->sock , client_message , strlen(client_message));

		//clear the message buffer
		memset(client_message, 0, 2000);
	}
	*/

	if(read_size == 0)
	{
		puts("Client disconnected");
		fflush(stdout);
	}
	
	else if(read_size == -1)
	{
		perror("recv failed");
	}
	return 0;
} 