
#include<stdio.h>
#include<string.h> //strlen
#include<stdlib.h> //strlen
#include<sys/socket.h>
#include<arpa/inet.h> //inet_addr
#include<unistd.h> //write
#include<pthread.h> //for threading , link with lpthread
//the thread function
pthread_mutex_t lock;

struct node
{
	int sock;
	char nama[100];
	struct node *next;
};

struct node *head, *tail;

void init()
{
    head=(struct node *)malloc(sizeof(*head));
    tail=(struct node *)malloc (sizeof(*tail));
    head->next=tail;
    tail->next=tail;
}


struct node* append(int nilai, char nama[], int read_size)
{
    struct node *ptr;
    struct node *t;
    ptr=head;
    while (ptr->next!=tail ) ptr=ptr->next;
    t=(struct node *)malloc(sizeof(*t));
    t->sock=nilai;

    char nametemp[read_size-2];
	bzero(nametemp,read_size-2);
	int i;
	for ( i=0; i<read_size-2; i++)
	{
		nametemp[i]=nama[i];
		printf("%c ",nametemp[i] );
	}

    strcpy(t->nama,nametemp);
    printf("nama di append: %s\n", nametemp);
    t->next=tail;
    ptr->next=t;
    return ptr;
}

void delete(struct node *ptr)
{
    struct node *t;
    t=ptr->next;
    ptr->next=ptr->next->next;
    free(t);

}

struct node * getnode(char nama[])
{
	struct node *ptr;
	ptr=head;
	while(ptr->next!=tail)
	{
		if (strcmp(ptr->nama,nama)==0)
		{
			break;
			return ptr;
		}
		ptr=ptr->next;
	}
	return 0;
}

void cetak()
{
	struct node *ptr;
	printf("username: \n");
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
	puts("Waiting for incoming connections...");
	c = sizeof(struct sockaddr_in);
	
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


void *connection_handler(void *socket_desc)
{
	//Get the socket descriptor
	char name[50];
	int flag=0;
	struct node * ptr;
	ptr= (struct node *) malloc(sizeof(*ptr));
	
	int sock = *(int*)socket_desc;
	int read_size;
	char *dest, pesan[2048],userdest[5];
	char *message , client_message[2000];
	//Send some messages to the client
	message="Write your username:  ";
	write(sock , message , strlen(message));
	read_size=recv(sock, name,1024, 0);
	
	printf("panjang %d\n", read_size);
	append(sock,name,read_size);
	cetak();
	message = "Greetings! I am your connection handler\n";
	write(sock , message , strlen(message));
	

	//Receive a message from client
	while( (read_size = recv(sock , client_message , 2000 , 0)) > 0 )
	{

		//end of string marker
		client_message[read_size] = '\0';
		printf("client_message: %s\n", client_message);
		strtok_r (client_message, " ", &dest);
		strcpy (pesan, dest);
		printf("destinasi string %s\n", pesan);
		//Send the message back to client
		ptr=getnode(client_message);
		printf("isi pesan string %s\n untuk %s  dengan sock %d", pesan,ptr->nama, ptr->sock);

		//Send the message back to client
		write(ptr->sock , pesan , strlen(pesan));

		//clear the message buffer
		memset(client_message, 0, 2000);
	}

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