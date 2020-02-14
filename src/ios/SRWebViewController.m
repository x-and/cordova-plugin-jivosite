//
//  SRWebViewController.m
//  Мой СевСтар
//
//  Created by Aleksandr Sviridov on 13.02.2020.
//

#import "SRWebViewController.h"


@implementation SRWebViewController {
    UIWebView *_webView;
}

- (void)loadView {
    self.view = _webView = [UIWebView new];
    _webView.delegate = self;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_webView loadRequest:[NSURLRequest requestWithURL:({
        [NSBundle.mainBundle URLForResource:@"index_ru" withExtension:@"html" subdirectory:@"www/jivosite"];
    })]];
}

- (UINavigationItem *)navigationItem {
    UINavigationItem *result = [[UINavigationItem alloc] initWithTitle:self.navigationTitle];
    result.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Back" style:UIBarButtonItemStylePlain target:self action:@selector(closeViewController)];
    return result;
}

- (void)closeViewController {
    [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    NSLog(@"%s", __PRETTY_FUNCTION__ );
    NSString *script = [NSString stringWithFormat:@"window.jivo_api.setUserToken(%ld)", (long)self.userId];
    [webView stringByEvaluatingJavaScriptFromString:script];
}

@end


@implementation SRWebNavigationController

@synthesize webViewController = _webViewController;

- (instancetype)init
{
    self = [super init];
    if ( !self ) {
        return self;
    }
    _webViewController = [SRWebViewController new];
    return self;
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    if ( @available(iOS 13.0, *) ) {
        return UIStatusBarStyleDarkContent;
    }
    return UIStatusBarStyleDefault;
}

- (void)showWebViewController {
    UIViewController *rootViewController = UIApplication.sharedApplication.keyWindow.rootViewController;
    rootViewController.modalPresentationStyle = UIModalPresentationPageSheet;
    [rootViewController presentViewController:({
        SRWebNavigationController *navigationController = [[SRWebNavigationController alloc] initWithRootViewController:_webViewController];
        navigationController;
    }) animated:YES completion:nil];
}

@end
