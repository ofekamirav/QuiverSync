//
//  SignInWithAppleCoordinator.swift
//  iosApp
//
//  Created by gal levi on 19/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import AuthenticationServices
import Shared

class SignInWithAppleCoordinator: NSObject {
    private let viewModel: LoginViewModel

    init(viewModel: LoginViewModel) {
        self.viewModel = viewModel
    }

    func startSignIn() {
        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]

        let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = self
        controller.presentationContextProvider = self
        controller.performRequests()
    }
}

extension SignInWithAppleCoordinator: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential,
           let identityToken = appleIDCredential.identityToken,
           let tokenString = String(data: identityToken, encoding: .utf8) {
            viewModel.onAppleSignInResult(token: tokenString)
        }
    }

    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        print("Apple sign-in failed: \(error.message)")
    }
}

extension SignInWithAppleCoordinator: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return UIApplication.shared
            .connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }!
    }

}
