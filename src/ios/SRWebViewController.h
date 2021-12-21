//
//  SRWebViewController.h
//  Мой СевСтар
//
//  Created by Aleksandr Sviridov on 13.02.2020.
//

@import UIKit;
#import <WebKit/WebKit.h>
@interface SRWebViewController : UIViewController<WKNavigationDelegate>

@property (nonatomic, copy) NSString *navigationTitle;
@property NSInteger userId;
@property NSString *payload;

@end


@interface SRWebNavigationController<__covariant VisibleController> : UINavigationController

@property (readonly) VisibleController visibleViewController;

- (void)showWebViewController;

+ (SRWebNavigationController<SRWebViewController *> *)defaultNavigationController;

@end

