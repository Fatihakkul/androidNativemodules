/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React,{useState} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  NativeModules,
  TouchableOpacity,
  Image
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

const App = () => {

  const [src,setSrc]=useState(null)

  return (
   <View>
      <TouchableOpacity
      
        onPress={()=>{
          NativeModules.ImagePickerModule.pickImage().then(res=>{
           setSrc(res)
          })}}
      >
        <Text>Pressssss</Text>
        <Image style={{width:100,height:100}}  source={{uri : src != null ? src : null}} />
      </TouchableOpacity>
      <TouchableOpacity
      
        onPress={()=>{
          NativeModules.ImagePickerModule.startRecord()}}
      >
        <Text>Pressssss</Text>
        <Image style={{width:100,height:100}}  source={{uri : src != null ? src : null}} />
      </TouchableOpacity>
      <TouchableOpacity
      
        onPress={()=>{
          NativeModules.ImagePickerModule.stop()}}
      >
        <Text>Pres ME</Text>
        <Image style={{width:100,height:100}}  source={{uri : src != null ? src : null}} />
      </TouchableOpacity>
   </View>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
