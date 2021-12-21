//
//  SRWebViewController.m
//  Мой СевСтар
//
//  Created by Aleksandr Sviridov on 13.02.2020.
//

#import "SRWebViewController.h"


@implementation SRWebViewController {
    WKWebView *_webView;
}

- (void)loadView {
    self.view = _webView = [WKWebView new];
    _webView.navigationDelegate = self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Back" style:UIBarButtonItemStylePlain target:self action:@selector(closeViewController)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_webView loadRequest:[NSURLRequest requestWithURL:({
        [NSBundle.mainBundle URLForResource:@"index_ru" withExtension:@"html" subdirectory:@"www/jivosite"];
    })]];
}

- (void)viewDidDisappear:(BOOL)animated {
    self.view = NULL;
}

- (void)setNavigationTitle:(NSString *)navigationTitle {
    _navigationTitle = [NSString stringWithFormat:@"%@", navigationTitle];
    self.navigationItem.title = self.title = navigationTitle;
}

- (void)closeViewController {
    [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - WKNavigationDelegate

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    NSLog(@"%s", __PRETTY_FUNCTION__ );
    NSString *script = [NSString stringWithFormat:@"window.setUserToken(%ld)", (long)self.userId];
    [webView evaluateJavaScript:script completionHandler:^(id result, NSError *error) {}];
    NSString *script2 = [NSString stringWithFormat:@"window.setPayload(%@)", self.payload];
    [webView evaluateJavaScript:script2 completionHandler:^(id result, NSError *error) {}];

    [webView evaluateJavaScript:@"window.setData('ru', false)" completionHandler:^(id result, NSError *error) {}];
}

@end


@implementation SRWebNavigationController

@synthesize visibleViewController = _visibleViewController;

- (instancetype)initWithRootViewController:(UIViewController *)rootViewController {
    self = [super initWithRootViewController:rootViewController];
    if ( !self ) {
        return self;
    }
    self.navigationBar.barStyle = UIBarStyleDefault;
    _visibleViewController = rootViewController;
    return self;
}

+ (instancetype)defaultNavigationController {
    return [[self alloc] initWithRootViewController:[SRWebViewController new]];
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    if ( @available(iOS 13.0, *) ) {
        return UIStatusBarStyleDarkContent;
    }
    return UIStatusBarStyleDefault;
}

- (void)showWebViewController {
    if ( [[self viewIfLoaded] window] ) {
        return;
    }
    [self dismissViewControllerAnimated:NO completion:^{
        UIViewController *rootViewController = UIApplication.sharedApplication.keyWindow.rootViewController;
        rootViewController.modalPresentationStyle = UIModalPresentationPageSheet;
        [rootViewController presentViewController:self animated:YES completion:nil];
    }];
}

@end
