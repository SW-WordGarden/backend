inteliJ만 설치하여 테스트하는 방법과 inteliJ설치+MySQL 설치하여 테스트하는 방법이 있습니다. 
코드는 inteliJ만 설치되었을 때 기준으로 되어있습니다. 
</br>
# 1. inteliJ설치
테스트용 DB를 사용하는 방법입니다. 데이터 보존을 보장하기 어렵습니다. 

https://www.jetbrains.com/ko-kr/idea/download/?section=windows
커뮤니티 버전 다운 받으시고 설치하시면 됩니다.
설치 과정에서 따로 설정해야하는 부분은 없습니다.

제 코드 git에서 pull 받으신 뒤 gradle 한번 새로고침 해주시고
실행시키고 테스트 하시면 됩니다.

실행은 WordgardenApplication.java 여기서 실행시키시면 되요!</br></br>
# 2. inteliJ설치+MySQL 설치
intelij는 위와 동일하게 설치해주시면 됩니다. 

https://dev.mysql.com/downloads/windows/installer/8.0.html
위 사이트에서 용량 더 큰 걸로 다운받아 주시고

https://dearmycode.tistory.com/15
이 사이트 보시고 설정 따라하시면 됩니다.

코드 pull받으신 뒤 application.propertiesdptj 
![](https://velog.velcdn.com/images/jiw0707/post/c33f899a-37d7-4bd7-b959-5fd0df5fcaa5/image.png)
H2설정을 주석처리 하시고 나머지 부분을 다 활성화 시키시면 됩니다.
url에서 port번호(현재 코드에서 3307이라고 되어 있는 부분)
username에 사용자 이름 작성해주시고
mysql에서 해당 사용자 계정을 생성할 때에 설정했던 password를 작성해주시면 됩니다.
