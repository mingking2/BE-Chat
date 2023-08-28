# [Toyproject] BE-AuthService

니가 해야할 것!

로그인, 회원가입 시스템을 완벽히 구현

1.  회원가입 => 닉네임 추가 (db도 당연히)
2. 로그인 => 로직 그대로
   프론트에서 로그인 하면 니가 세션 id 반환해주고(쿠키로) 클라이언트에서 /chat 으로 라우팅(승제가)
3. /chat에서 엄청나게 많은 api 요청을 할 거야 그에 대한 api를 수요일까지 만들어야돼

4. 일단
   profile 컴포넌트 : 프로필 사진 url 보내주기(미정, 근데 해야됨) = profile/image
   <br>닉네임 가져오기 = profile/nickname
   <br>상태 메세지 가져오기 + 상태메세지 추가 요청 시 db에 저장해주는 api 구현 = profile/status
   <br> 비밀번호 변경 api만들기(기존비번, 새비번을 보낼거임)   = profile/pwChange
   <br>=> 그러면 클라이언트 쪽에서 res로 받고 재 로그인 하게끔 하겠음

friends 컴포넌트 : 친구 추가 요청( 닉네임 보내면 있는지 찾아서 있으면 이름,상태메세지 보내주기)+(db에서 외래키로 친구 추가 로직 구현)
= friends/addFriend
<br><br>
chat 컴포넌트 : 채팅 추가 요청 ( 닉네임 보내면 있는지 찾아서(이미 있으면 알림) 없으면 닉네임 보내주기 )+( db에 채팅 정보 추가 )
= chat/addChat
<br>
수요일까지 (마지노선이 목요일) 챗을 제외한 모든 api를 구현

목요일부터 금요일까지 웹소켓 챗 구현

토요일에 전체 css 점검, 나머지 부수적인 것들 구현 + 발표자료 준비(밤새서)




