package team.capstonelongstone.freetraveler.auth.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.capstonelongstone.freetraveler.account.domain.Account;
import team.capstonelongstone.freetraveler.auth.login.dto.LoginDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author 정순범
 * 로그인시 입력폼에서 가져올 값 DTO
 */
@Service
public class LoginService {

    private final LoginRepository loginRepository;

    @Autowired
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    /**
     * 로그인 로직
     */
    public ResponseEntity login(LoginDTO loginDTO, HttpServletResponse response, HttpServletRequest request){
        Account account = loginRepository.findByUserId(loginDTO.getUserId());
        if (Objects.isNull(account)){ //아이디 없을 때
            return new ResponseEntity("회원 아이디 없음",HttpStatus.BAD_REQUEST);
        }

        else{
            if(loginDTO.getUserPassword().equals(account.getUserPassword())) { //로그인 성공
                HttpSession session=request.getSession();
                session.setAttribute("account",account);
                return new ResponseEntity(HttpStatus.OK);
            }
            else{ //로그인 실패
                return new ResponseEntity("로그인 실패",HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * 로그인 체크 여부 로직
     */
    public ResponseEntity checkLogin(HttpServletRequest request,HttpServletResponse response){
        HttpSession session=request.getSession();
        Account account = (Account) session.getAttribute("account");

        if(Objects.isNull(account)){
            return new ResponseEntity("세션이 없습니다.", HttpStatus.valueOf(401));
        }
        else{
            return new ResponseEntity(account.getUserId(), HttpStatus.OK);
        }

    }
}