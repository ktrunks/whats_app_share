import 'package:flutter/material.dart';
import 'dart:async';
import 'package:whats_app_share/whats_app_share.dart';
import 'dart:io';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:screenshot/screenshot.dart';

void main() => runApp(MyApp());

// ignore: must_be_immutable
class MyApp extends StatelessWidget {
  final _controller = ScreenshotController();
  late File _image;

  Future<void> share() async {
    await WhatsAppShare.share(
      text: 'Example share text',
      linkUrl: 'https://flutter.dev/',
      phone: '911234567890',
    );
  }

  Future<void> shareFile() async {
    await getImage();
    Directory? directory;
    if (Platform.isAndroid) {
      directory = await getExternalStorageDirectory();
    } else {
      directory = await getApplicationDocumentsDirectory();
    }
    print('${directory!.path} / ${_image.path}');
    await WhatsAppShare.shareFile(
      text: 'Whatsapp message text',
      phone: '911234567890',
      filePath: ["${_image.path}"],
    );
  }

  Future<void> isInstalled() async {
    final val = await WhatsAppShare.isInstalled();
    print('Whatsapp is installed: $val');
  }

  Future<void> shareScreenShot() async {
    Directory? directory;
    if (Platform.isAndroid) {
      directory = await getExternalStorageDirectory();
    } else {
      directory = await getApplicationDocumentsDirectory();
    }
    final String localPath =
        '${directory!.path}/${DateTime.now().toIso8601String()}.png';

    await _controller.capture(delay: Duration(milliseconds: 10));

    await Future.delayed(Duration(seconds: 1));

    await WhatsAppShare.shareFile(
      text: 'Whatsapp message text',
      phone: '911234567890',
      filePath: [localPath],
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Whatsapp Share'),
        ),
        body: Center(
          child: Screenshot(
            controller: _controller,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                ElevatedButton(
                  child: Text('Share text and link'),
                  onPressed: share,
                ),
                ElevatedButton(
                  child: Text('Share Image'),
                  onPressed: shareFile,
                ),
                ElevatedButton(
                  child: Text('Share screenshot'),
                  onPressed: shareScreenShot,
                ),
                ElevatedButton(
                  child: Text('is Installed'),
                  onPressed: isInstalled,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  ///Pick Image From gallery using image_picker plugin
  Future getImage() async {
    try {
      File _pickedFile =
      (await ImagePicker().pickImage(source: ImageSource.gallery)) as File;

      if (_pickedFile != null) {
        final directory = await getExternalStorageDirectory();
        _image = await _pickedFile.copy('${directory!.path}/image1.png');
      } else {}
    } catch (er) {
      print(er);
    }
  }
}
