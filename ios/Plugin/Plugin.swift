import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(KakaoIntentHandler)
public class KakaoIntentHandler: CAPPlugin {
    let SCHEME_KAKAOTALK = "/scheme/kakaotalk"

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": value
        ])
    }
    
    public override func shouldOverrideLoad(_ navigationAction: WKNavigationAction!) -> NSNumber! {
        var navUrl = navigationAction.request.url!
        let url = navUrl
        let isKakaotalkScheme = navUrl.path.starts(with: SCHEME_KAKAOTALK)
        
        // 카카오톡 로그인 이후 들어오는 분기
        if (navUrl.scheme?.starts(with: "capacitor") == true) {
            navUrl = URL.init(string: navUrl.absoluteString.replacingOccurrences(of: CAPBridge.CAP_DEFAULT_SCHEME, with: "https"))!
            let request = URLRequest(url: navUrl)
            // api.moranique.com 으로 넘어오는 url을 웹뷰로 로드시켜준다.
            webView.load(request)
            return true
        }

        // 카카오톡 딥링크 (카카오링크)
        if ["kakaolink"].contains(navUrl.scheme) {
            // 카카오톡 실행 가능 여부 확인 후 실행
            if UIApplication.shared.canOpenURL(navUrl) {
                UIApplication.shared.open(navUrl, options: [:], completionHandler: nil)
            }
            return true
        }
        
        
        // 카카오톡 소셜 로그인
        if (isKakaotalkScheme) {
            // 카카오톡이 설치된 경우
            if UIApplication.shared.canOpenURL(url) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
                return true;
            } else {
                let request = URLRequest(url: url)
                self.webView.load(request)
                return true;
            }
        }
        return nil
    }
}
