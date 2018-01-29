#include <iostream>
#include<string.h>
#include<netinet/in.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<arpa/inet.h>
#include<unistd.h>

using namespace std;

void client()
{
    const unsigned short SERVERPORT = 53556;
    const int MAXSIZE = 1024;
    const char* SERVER_IP = "192.168.3.4";
    const char* DATA = "this is a client message";

    int sock, recvBytes;
    char buf[MAXSIZE];
//    hostent *host;
    sockaddr_in serv_addr;

    if( (sock = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        cerr<<"socket create fail!"<<endl;
        exit(1);
    }
    bzero( &serv_addr, sizeof(serv_addr) );
    serv_addr.sin_family =  AF_INET;
    serv_addr.sin_port = htons(SERVERPORT);
    serv_addr.sin_addr.s_addr = inet_addr(SERVER_IP);

    if( connect(sock, (sockaddr*)&serv_addr, sizeof(sockaddr)) == -1)
    {
        cerr<<"connect error"<<endl;
        exit(1);
    }

    write(sock, const_cast<char*>(DATA), strlen(DATA) );
    if( (recvBytes = recv(sock, buf, MAXSIZE, 0)) == -1)
    {
        cerr<<"recv error!"<<endl;
        exit(1);
    }

    buf[recvBytes] = '\0';
    cout<<buf<<endl;
    close(sock);
}

int main() {
    client();
    return 0;
}