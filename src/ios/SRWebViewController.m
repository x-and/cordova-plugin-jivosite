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

- (void)showWebViewController {
    UIViewController *rootViewController = UIApplication.sharedApplication.keyWindow.rootViewController;
    rootViewController.modalPresentationStyle = UIModalPresentationPageSheet;
    [rootViewController presentViewController:({
        UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:self];
        navigationController;
    }) animated:YES completion:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_webView loadRequest:[NSURLRequest requestWithURL:({
        [NSBundle.mainBundle URLForResource:@"index_ru" withExtension:@"html" subdirectory:@"www/assets/jivochat"];
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
