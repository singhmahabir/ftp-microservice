///**
// * All rights reserved.
// */
//
//package singh.mahabir.ftp.security;
//
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.ClientRegistrationException;
//import org.springframework.stereotype.Service;
//
//import singh.mahabir.ftp.repository.Client;
//import singh.mahabir.ftp.repository.ClientRepository;
//import singh.mahabir.ftp.repository.MyClientDetails;
//
///**
// * @author Mahabir Singh
// *
// */
//@Service
//public class MyClientDetailsService implements ClientDetailsService {
//
//    @Autowired
//    private ClientRepository clientRepository;
//
//    @Override
//    public ClientDetails loadClientByClientId(String clientId) {
//	Optional<Client> client = clientRepository.findByClientId(clientId);
//
//	return client.map(MyClientDetails::new)
//		.orElseThrow(() -> new ClientRegistrationException("Not found: " + clientId));
//    }
//
//}
