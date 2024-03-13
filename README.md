# embedded_sys_project
Repository for documentation of our Embbedded System Project discipline at IBMEC - Barra


![image](https://github.com/bebonzoumet/embedded_sys_project/assets/142441297/afa64e8e-2cb8-4605-a0aa-cd2e8d4ac8ee)

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Começamos os testes com a placa ESP-32-Wrover da FreeNove;

Utlizamos o datasheet a seguir da própria freenove para conseguirmos ver a pinagem e os botões de Boot e RST(Reset):

![image](https://makeradvisor.com/wp-content/uploads/2023/02/Freenove-ESP32-Wrover-CAM-pinout.jpg)

Os testes que foram feitos na placa foram com os LEDs e testando a câmera propriamente dita. Todos os sketches podem ser encontrados nesse link do repositório: https://github.com/Freenove/Freenove_ESP32_WROVER_Board/tree/main/C/Sketches

Para o teste do LED o Sketch ultilizado foi o Sketch_01.1_Blink(https://github.com/Freenove/Freenove_ESP32_WROVER_Board/tree/main/C/Sketches/Sketch_01.1_Blink);

Para o teste da câmera foi utilizado o Sketch_06.1_CameraWebServer(https://github.com/Freenove/Freenove_ESP32_WROVER_Board/tree/main/C/Sketches/Sketch_06.1_CameraWebServer), no começo tivemos problemas como:
- Conexão com a câmera: Usamos o Acesso Pessoal para conseguir gerar um IP para nos conectarmos com a câmera para ver a imagem sendo demonstrada, porém quando usamos pelo Iphone o Firewall da Apple não permitiu para que o IP fosse mostrado, então mudamos para o Android e foi normalmente, segue em seguida um print da tela da câmera 

<img width="866" alt="image" src="https://github.com/bebonzoumet/embedded_sys_project/assets/82557298/380bef09-4f67-432a-b876-601fd6df502f">








https://github.com/RuiSantosdotme/ESP32-CAM-Arduino-IDE/blob/master/ESP32-CAM-Video-Streaming/ESP32-CAM-Access-Point-AP-Video-Streaming.ino
