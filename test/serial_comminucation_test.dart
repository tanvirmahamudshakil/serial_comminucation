import 'package:flutter_test/flutter_test.dart';
import 'package:serial_comminucation/serial_comminucation.dart';
import 'package:serial_comminucation/serial_comminucation_platform_interface.dart';
import 'package:serial_comminucation/serial_comminucation_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSerialComminucationPlatform
    with MockPlatformInterfaceMixin
    implements SerialComminucationPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final SerialComminucationPlatform initialPlatform = SerialComminucationPlatform.instance;

  test('$MethodChannelSerialComminucation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSerialComminucation>());
  });

  test('getPlatformVersion', () async {
    SerialComminucation serialComminucationPlugin = SerialComminucation();
    MockSerialComminucationPlatform fakePlatform = MockSerialComminucationPlatform();
    SerialComminucationPlatform.instance = fakePlatform;

    expect(await serialComminucationPlugin.getPlatformVersion(), '42');
  });
}
