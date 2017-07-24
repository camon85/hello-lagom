import akka.NotUsed;
import com.camon.user.api.User;
import com.camon.user.api.UserService;
import com.lightbend.lagom.javadsl.api.ServiceCall;

public class UserServiceImpl implements UserService{
    @Override
    public ServiceCall<NotUsed, User> getUser(String userId) {
        return null;
    }

    @Override
    public ServiceCall<User, NotUsed> createUser() {
        return null;
    }
}
