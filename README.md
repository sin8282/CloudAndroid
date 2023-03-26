# CloudAndroid
 
안드로이드 클라우드 서비스 제작 - 개인 공부용

### 1. 기능<br>

😊 로그인 : 구글로그인<br><br>
![로그인](https://user-images.githubusercontent.com/57480215/227797430-cd9f3e93-effd-49f7-a2ec-cdff2d6b8838.gif)<br><br>
😊 업로드 기능 : 종료해도 백그라운드에서 실행.<br><br>
![업로드](https://user-images.githubusercontent.com/57480215/227797443-e85b31d0-a5bf-4682-829b-c76c95cc84b6.gif)<br><br>
😊 다운로드 기능 : 업로드랑 동일 Pic폴더에 shuCloud폴더 생성 및 저장<br><br>
![다운로드](https://user-images.githubusercontent.com/57480215/227797425-6ab869e7-1542-44d9-ae4a-a02b5e5754d5.gif)<br><br>
😊 폴더 공유 기능 : 친구추가 기능 사용가능 및 권한 설정 가능 ( 저장권한, 삭제권한, 내자료만 보기)<br><br>
![폴더권한설정](https://user-images.githubusercontent.com/57480215/227797451-5fc2aab4-6142-4060-894e-a93da92a8396.gif)<br><br>
😊 폴더 생성 및 수정 :<br><br>
![폴더생성](https://user-images.githubusercontent.com/57480215/227797456-c646152a-037f-4cb3-b6a4-40bcaa7b54d1.gif)
![폴더수정](https://user-images.githubusercontent.com/57480215/227797460-1a4f278a-328d-4b59-9c5c-dbdf81ea545d.gif)<br><br>
😊 폴더 및 파일 삭제 :<br><br>
![폴더및파일삭제](https://user-images.githubusercontent.com/57480215/227797452-e08033ba-45af-44b9-a6cd-c59e2ee4e1d5.gif)<br><br>
<br><br>

### 2. 적용 
 -구글 로그인에 필요한 api 및 credentials.json 필요<br>
 -AndroidStudioProjects에 BASE_URL_API 아이피 등록필요<br>
 ``` 
  private static final String BASE_URL_API
 ``` 
 -CloudAndroid/shuCloud/ 프로젝트, AndroidController.java에서 수정필요<br>
 ``` 
  private static final String ROOT_DIR;
  private static final String SERVER_DIR;
  private static final String PROFILE_DIR;
  private static final String S = "/";
 ```
