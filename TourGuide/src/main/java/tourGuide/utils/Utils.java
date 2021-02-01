package tourGuide.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.exception.UserNoTFoundException;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

@Service
public class Utils {

	@Autowired
	private TourGuideService tourGuideService;
	
	public User getUser(String userName) throws UserNoTFoundException {
		return tourGuideService.getUser(userName);
	}
	
}
