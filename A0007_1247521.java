import java.net.*;
import java.io.*;

public class SEOUL07_JAVA_고민서 {

	// 닉네임을 사용자에 맞게 변경해 주세요.
	static final String NICKNAME = "SEOUL07_JAVA_고민서";
	
	// 일타싸피 프로그램을 로컬에서 실행할 경우 변경하지 않습니다.
	static final String HOST = "127.0.0.1";

	// 일타싸피 프로그램과 통신할 때 사용하는 코드값으로 변경하지 않습니다.
	static final int PORT = 1447;
	static final int CODE_SEND = 9901;
	static final int CODE_REQUEST = 9902;
	static final int SIGNAL_ORDER = 9908;
	static final int SIGNAL_CLOSE = 9909;

	// 게임 환경에 대한 상수입니다.
	static final int TABLE_WIDTH = 254;
	static final int TABLE_HEIGHT = 127;
	static final int NUMBER_OF_BALLS = 6;
	static final int[][] HOLES = { { 0, 0 }, { 127, 0 }, { 254, 0 }, { 0, 127 }, { 127, 127 }, { 254, 127 } };

	public static void main(String[] args) {

		Socket socket = null;
		String recv_data = null;
		byte[] bytes = new byte[1024];
		float[][] balls = new float[NUMBER_OF_BALLS][2];
		int order = 0;

		try {
			socket = new Socket();
			System.out.println("Trying Connect: " + HOST + ":" + PORT);
			socket.connect(new InetSocketAddress(HOST, PORT));
			System.out.println("Connected: " + HOST + ":" + PORT);

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			String send_data = CODE_SEND + "/" + NICKNAME + "/";
			bytes = send_data.getBytes("UTF-8");
			os.write(bytes);
			os.flush();
			System.out.println("Ready to play!\n--------------------");

			while (socket != null) {

				// Receive Data
				bytes = new byte[1024];
				int count_byte = is.read(bytes);
				recv_data = new String(bytes, 0, count_byte, "UTF-8");
				System.out.println("Data Received: " + recv_data);

				// Read Game Data
				String[] split_data = recv_data.split("/");
				int idx = 0;
				try {
					for (int i = 0; i < NUMBER_OF_BALLS; i++) {
						for (int j = 0; j < 2; j++) {
							balls[i][j] = Float.parseFloat(split_data[idx++]);
						}
					}
				} catch (Exception e) {
					bytes = (CODE_REQUEST + "/" + CODE_REQUEST).getBytes("UTF-8");
					os.write(bytes);
					os.flush();
					System.out.println("Received Data has been currupted, Resend Requested.");
					continue;
				}

				// Check Signal for Player Order or Close Connection
				if (balls[0][0] == SIGNAL_ORDER) {
					order = (int)balls[0][1];
					System.out.println("\n* You will be the " + (order == 1 ? "first" : "second") + " player. *\n");
					continue;
				} else if (balls[0][0] == SIGNAL_CLOSE) {
					break;
				}

				// Show Balls' Position
				for (int i = 0; i < NUMBER_OF_BALLS; i++) {
					System.out.println("Ball " + i + ": " + balls[i][0] + ", " + balls[i][1]);
				}

				float angle = 0.0f;
				float power = 0.0f;

				//////////////////////////////
				// 이 위는 일타싸피와 통신하여 데이터를 주고 받기 위해 작성된 부분이므로 수정하면 안됩니다.
				//
				// 모든 수신값은 변수, 배열에서 확인할 수 있습니다.
				//   - order: 1인 경우 선공, 2인 경우 후공을 의미
				//   - balls[][]: 일타싸피 정보를 수신해서 각 공의 좌표를 배열로 저장
				//     예) balls[0][0]: 흰 공의 X좌표
				//         balls[0][1]: 흰 공의 Y좌표
				//         balls[1][0]: 1번 공의 X좌표
				//         balls[4][0]: 4번 공의 X좌표
				//         balls[5][0]: 마지막 번호(8번) 공의 X좌표
				
				// 여기서부터 코드를 작성하세요.
				// 아래에 있는 것은 샘플로 작성된 코드이므로 자유롭게 변경할 수 있습니다.
				





				// whiteBall_x, whiteBall_y: 흰 공의 X, Y좌표를 나타내기 위해 사용한 변수
				float whiteBall_x = balls[0][0];
				float whiteBall_y = balls[0][1];
				
				// targetBall_x, targetBall_y: 목적구의 X, Y좌표를 나타내기 위해 사용한 변수
				float targetBall_x = balls[1][0];
				float targetBall_y = balls[1][1];
				for(int b=1;b<6;b++) {
					if(balls[b][0]>0 && balls[b][1]>0) {
						targetBall_x = balls[b][0];
						targetBall_y = balls[b][1];
						break;
					}
				}

				// width, height: 목적구와 흰 공의 X좌표 간의 거리, Y좌표 간의 거리
				float width = Math.abs(targetBall_x - whiteBall_x);
				float height = Math.abs(targetBall_y - whiteBall_y);
				
				// 피타고라스 정리를 이용해서 구한 빗변 길이
				float length = (float) Math.sqrt(width*width + height*height);
				
				// radian: width와 height를 두 변으로 하는 직각삼각형의 각도를 구한 결과
				//   - 1radian = 180 / PI (도)
				//   - 1도 = PI / 180 (radian)
				// angle : 아크탄젠트로 얻은 각도 radian을 degree로 환산한 결과
				double radian = height > 0? Math.atan(width / height): 0;
				angle = (float) ((180.0 / Math.PI) * radian);
				
				// targetHole_x, targetHole_y : 목적구를 넣을 홀 좌표
				float targetHole_x=0;
				float targetHole_y=0;
				// 목적구가 흰 공을 중심으로 2사분면에 위치하면서 x좌표가 127 미만
				if(whiteBall_x > targetBall_x && whiteBall_y < targetBall_y && targetBall_x<127) {
					targetHole_x = 0;
					targetHole_y = 0;
				}else if(whiteBall_x > targetBall_x && whiteBall_y > targetBall_y && targetBall_x<127){
					// 목적구가 흰 공을 중심으로 3사분면에 위치
					targetHole_x = 0;
					targetHole_y = 127;
				}else if((whiteBall_x < targetBall_x && whiteBall_y < targetBall_y && targetBall_x<127)
						|| (whiteBall_x > targetBall_x && whiteBall_y < targetBall_y && targetBall_x>127)) {
					// 목적구가 흰 공을 중심으로 1사분면에 위치하고 x좌표가 127 미만, 2사분면에 위치하고 127초과
					targetHole_x = 127;
					targetHole_y = 127;
				}else if((whiteBall_x < targetBall_x && whiteBall_y > targetBall_y && targetBall_x<127)
						|| (whiteBall_x > targetBall_x && whiteBall_y > targetBall_y && targetBall_x>127)) {
					// 목적구가 흰 공을 중심으로 4사분면에 위치하고 x좌표가 127 미만, 3사분면에 위치하고 127초과
					targetHole_x = 127;
					targetHole_y = 0;
				}else if(whiteBall_x < targetBall_x && whiteBall_y < targetBall_y && targetBall_x>127){
					// 목적구가 흰 공을 중심으로 1사분면에 위치하면서 x좌표가 127 초과
					targetHole_x = 254;
					targetHole_y = 127;
				}else if(whiteBall_x < targetBall_x && whiteBall_y > targetBall_y && targetBall_x>127){
					// 목적구가 흰 공을 중심으로 4사분면에 위치
					targetHole_x = 254;
					targetHole_y = 0;
				}

				// 목적구와 목적홀 사이의 거리 계산
				float twidth = Math.abs(targetBall_x - targetHole_x);
				float theight = Math.abs(targetBall_y - targetHole_y);
				
				// 목적구와 목적홀 사이의 각도 계산
				double tradian = height > 0? Math.atan(width / height): 0;
				float tangle = (float) ((180.0 / Math.PI) * radian);
				
				// 목적구가 흰공을 중심으로 1사분면에 위치해 있을 때 기본 각도 조정
				// 당구공 직경 5.73
				if(angle>tangle) {
					// 조정된 흰 공과 기존 흰 공 사이의 거리
					float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(angle-tangle));
					float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
					angle = angle+adjangle;
				}else if(angle<tangle){
					float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(tangle-angle));
					float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
					angle = angle-adjangle;
				}
				
				
				// 목적구가 상하좌우로 일직선상에 위치했을 때 각도 입력
				if (whiteBall_x == targetBall_x)
				{
					if (whiteBall_y < targetBall_y)
					{
						angle = 0;
					}
					else
					{
						angle = 180;
					}
				}
				else if (whiteBall_y ==targetBall_y)
				{
					if (whiteBall_x < targetBall_x)
					{
						angle = 90;
					}
					else
					{
						angle = 270;
					}
				}
				
				// 목적구가 흰 공을 중심으로 2사분면에 위치했을 때 각도를 재계산
				if (whiteBall_x > targetBall_x && whiteBall_y < targetBall_y) {
					radian = Math.atan(height / width);
					angle = (float) (((180.0 / Math.PI) * radian));
					
					// 각도 조정
					// 목적 홀과 목적구 사이의 각도 계산
					
					tradian = Math.atan(height / width);
					tangle = (float) ((180.0 / Math.PI) * radian);
					if(angle>tangle) {
						// 조정된 흰 공과 흰 공 사이의 거리
						float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(angle-tangle));
						float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
						angle = angle+adjangle;
					}else if(angle<tangle){
						float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(tangle-angle));
						float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
						angle = angle-adjangle;
					}
					
					angle = (angle + 270);
				}

				// 목적구가 흰 공을 중심으로 3사분면에 위치했을 때 각도를 재계산
				else if (whiteBall_x > targetBall_x && whiteBall_y > targetBall_y)
				{
					radian = Math.atan(width / height);
					angle = (float) (((180.0 / Math.PI) * radian));
					
					// 각도 조정
					// 목적 홀과 목적구 사이의 각도 계산
					if(angle>tangle) {
						// 조정된 흰 공과 흰 공 사이의 거리
						float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(angle-tangle));
						float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
						angle = angle+adjangle;
					}else if(angle<tangle) {
						float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(tangle-angle));
						float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
						angle = angle-adjangle;
					}
					
					angle = angle + 180;
				}

				// 목적구가 흰 공을 중심으로 4사분면에 위치했을 때 각도를 재계산
				else if (whiteBall_x < targetBall_x && whiteBall_y > targetBall_y)
				{
					radian = Math.atan(height / width);
					angle = (float) (((180.0 / Math.PI) * radian));
					
					// 각도 조정
					// 목적 홀과 목적구 사이의 각도 계산
					tradian = Math.atan(height / width);
					tangle = (float) ((180.0 / Math.PI) * radian);
					
					if(angle>tangle) {
						// 조정된 흰 공과 흰 공 사이의 거리
						float tlength = (float) Math.sqrt(5.73*10.73 + length*length - 2*5.73*length*Math.cos(angle-tangle));
						float adjangle = (float) Math.acos(5.73*10.73-length*length-tlength*tlength/(2*length*tlength));
						angle = angle+adjangle;
					}else if(angle<tangle) {
						float tlength = (float) Math.sqrt(5.73*5.73 + length*length - 2*5.73*length*Math.cos(tangle-angle));
						float adjangle = (float) Math.acos(5.73*5.73-length*length-tlength*tlength/(2*length*tlength));
						angle = angle-adjangle;
					}
					
					angle = angle + 90;
				}
				
				// distance: 두 점(좌표) 사이의 거리를 계산
				double distance = Math.sqrt((width * width) + (height * height));

				// power: 거리 distance에 따른 힘의 세기를 계산
				power = (float) distance;
				// 힘이 너무 작거나, 수직/수평으로 칠 경우 최대 힘으로 치기
				if(power<20 || angle==0||angle==90||angle==180||angle==270) {
					power = 100;
				}
				


				
				
				// 주어진 데이터(공의 좌표)를 활용하여 두 개의 값을 최종 결정하고 나면,
				// 나머지 코드에서 일타싸피로 값을 보내 자동으로 플레이를 진행하게 합니다.
				//   - angle: 흰 공을 때려서 보낼 방향(각도)
				//   - power: 흰 공을 때릴 힘의 세기
				// 
				// 이 때 주의할 점은 power는 100을 초과할 수 없으며,
				// power = 0인 경우 힘이 제로(0)이므로 아무런 반응이 나타나지 않습니다.
				//
				// 아래는 일타싸피와 통신하는 나머지 부분이므로 수정하면 안됩니다.
				//////////////////////////////

				String merged_data = angle + "/" + power + "/";
				bytes = merged_data.getBytes("UTF-8");
				os.write(bytes);
				os.flush();
				System.out.println("Data Sent: " + merged_data);
			}
			os.close();
			is.close();
			socket.close();
			System.out.println("Connection Closed.\n--------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
