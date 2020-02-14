//
//  SRWebViewController.h
//  Мой СевСтар
//
//  Created by Aleksandr Sviridov on 13.02.2020.
//

@import UIKit;

@interface SRWebViewController : UIViewController<UIWebViewDelegate>

@property (copy) NSString *navigationTitle;
@property NSInteger userId;

@end


@interface SRWebNavigationController : UINavigationController

@property (readonly) SRWebViewController *webViewController;

- (void)showWebViewController;

@end

