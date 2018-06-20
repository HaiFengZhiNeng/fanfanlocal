package com.fanfan.robot.local.utils;

import android.content.Context;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.NovelApp;
import com.fanfan.robot.local.db.manager.LocalDBManager;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.listener.base.recog.GrammerUtils;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.ui.voice.AddVoiceActivity;
import com.fanfan.robot.local.utils.system.AppUtil;
import com.hankcs.hanlp.HanLP;
import com.robot.seabreeze.log.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class LoadDataUtils {

    public static List<LocalBean> loadLocalBean(Context context) throws IOException, ParserConfigurationException, SAXException {

        List<LocalBean> localBeanList = new ArrayList<>();

        InputStream inStream = context.getAssets().open("local.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inStream);

        NodeList businessList = document.getElementsByTagName("business");
        for (int i = 0; i < businessList.getLength(); i++) {

            Element businessElement = (Element) businessList.item(i);
            NamedNodeMap businessMap = businessElement.getAttributes();
            if (businessMap != null) {
                String name = businessMap.getNamedItem("name").getTextContent();

                String type = businessMap.getNamedItem("type").getTextContent();

                NodeList localNodes = businessElement.getChildNodes();
                for (int j = 0; j < localNodes.getLength(); j++) {

                    Node localNode = localNodes.item(j);

                    NamedNodeMap localMap = localNode.getAttributes();
                    if (localMap != null) {
                        LocalBean localBean = new LocalBean();

                        String id = localMap.getNamedItem("id").getTextContent();
                        String title = localMap.getNamedItem("title").getTextContent();
                        String detail = localMap.getNamedItem("detail").getTextContent();
                        String telephone = localMap.getNamedItem("telephone").getTextContent();
                        String lat = localMap.getNamedItem("lat").getTextContent();
                        String lng = localMap.getNamedItem("lng").getTextContent();


                        localBean.setShowTitle(title);
                        localBean.setShowDetail(detail);
                        localBean.setTelephone(telephone);
                        localBean.setLat(Double.valueOf(lat));
                        localBean.setLng(Double.valueOf(lng));
                        localBean.setSaveTime(System.currentTimeMillis());
                        localBean.setType(Integer.valueOf(type));
                        localBean.setBusiness(name);
                        localBeanList.add(localBean);
                        Log.e("id :  " + id);
                    }
                }
            }
        }

        return localBeanList;
    }

    public static List<VoiceBean> loadVoiceBean() {
        List<VoiceBean> voiceBeanList = new ArrayList<>();

        String[] localVoiceQuestion = resArray(R.array.local_voice_question);
        String[] localVoiceAnswer = resArray(R.array.local_voice_answer);

        for (int i = 0; i < localVoiceQuestion.length; i++) {
            VoiceBean voiceBean = new VoiceBean();

            String showTitle = localVoiceQuestion[i];
            voiceBean.setSaveTime(System.currentTimeMillis());
            voiceBean.setShowTitle(showTitle);
            voiceBean.setVoiceAnswer(localVoiceAnswer[i]);

            List<String> keywordList = HanLP.extractKeyword(localVoiceQuestion[i], 5);
            if (keywordList != null && keywordList.size() > 0) {
                Log.i(keywordList);
                voiceBean.setKeyword(GsonUtil.GsonString(keywordList));

                if (keywordList.size() == 1) {
                    voiceBean.setKey1(showTitle);
                } else {
                    for (int j = 0; j < keywordList.size(); j++) {
                        String key = keywordList.get(j);
                        if (j == 0) {
                            voiceBean.setKey1(key);
                        } else if (j == 1) {
                            voiceBean.setKey2(key);
                        } else if (j == 2) {
                            voiceBean.setKey3(key);
                        } else if (j == 4) {
                            voiceBean.setKey4(key);
                        }
                    }
                }
            } else {
                voiceBean.setKey1(showTitle);
            }

            voiceBeanList.add(voiceBean);
        }
        return voiceBeanList;
    }

    public static List<VoiceBean> loadExcelVoiceBean(String workPath) throws IOException, BiffException {

        File newFile = new File(workPath);
        if (!newFile.exists()) {
            return null;
        }

        Workbook workbook = Workbook.getWorkbook(newFile);
        Sheet sheet = workbook.getSheet(0);
        int sheetRows = sheet.getRows();

        List<VoiceBean> voiceBeanList = new ArrayList<>();

        for (int i = 1; i < sheetRows; i++) {

            VoiceBean bean = new VoiceBean();

            String showTitle = sheet.getCell(1, i).getContents().trim();

            bean.setSaveTime(System.currentTimeMillis());
            bean.setShowTitle(showTitle);
            bean.setVoiceAnswer(sheet.getCell(2, i).getContents().trim());

            //截取关键字
            List<String> keywordList = HanLP.extractKeyword(showTitle, 5);
            if (keywordList != null && keywordList.size() > 0) {
                Log.i(keywordList);
                bean.setKeyword(GsonUtil.GsonString(keywordList));

                if (keywordList.size() == 1) {
                    bean.setKey1(showTitle);
                } else {
                    for (int j = 0; j < keywordList.size(); j++) {
                        String key = keywordList.get(j);
                        if (j == 0) {
                            bean.setKey1(key);
                        } else if (j == 1) {
                            bean.setKey2(key);
                        } else if (j == 2) {
                            bean.setKey3(key);
                        } else if (j == 4) {
                            bean.setKey4(key);
                        }
                    }
                }
            } else {
                bean.setKey1(showTitle);
            }

            voiceBeanList.add(bean);
        }
        workbook.close();

        return voiceBeanList;
    }


    public static String getLexiconContents(Context context, VoiceDBManager voiceDBManager, LocalDBManager localDBManager) {

        //语法更新
        //存放三类语法中的关键词
        Set<String> setL1 = new HashSet<>();
        Set<String> setL2 = new HashSet<>();
        Set<String> setL3 = new HashSet<>();
        Set<String> setL4 = new HashSet<>();

        List<VoiceBean> voiceBeanList = voiceDBManager.loadAll();
        for (VoiceBean bean : voiceBeanList) {
            if (bean.getKey1() != null)
                setL1.add(bean.getKey1());
            if (bean.getKey2() != null)
                setL2.add(bean.getKey2());
            if (bean.getKey3() != null)
                setL3.add(bean.getKey3());
            if (bean.getKey4() != null)
                setL4.add(bean.getKey4());
        }

        //读取语法文件
        String localGrammar = GrammerUtils.getLocalGrammar(context);

        //程序中
        StringBuilder sbLocal = GrammerUtils.insertList(new StringBuilder(localGrammar),
                AppUtil.getLocalStrings(), GrammerUtils.getIndexL1(localGrammar));
        //地图
        List<LocalBean> localBeanList = localDBManager.loadAll();
        int mapIndex = GrammerUtils.getIndexL1(sbLocal);
        for (LocalBean localBean : localBeanList) {
            sbLocal.insert(mapIndex, "|" + localBean.getShowTitle());
        }
        //关键词
        StringBuilder sbL1 = GrammerUtils.insertSet(sbLocal, setL1, GrammerUtils.getIndexL1(sbLocal));
        StringBuilder sbL2 = GrammerUtils.insertSet(sbL1, setL2, GrammerUtils.getIndexL2(sbL1));
        StringBuilder sbL3 = GrammerUtils.insertSet(sbL2, setL3, GrammerUtils.getIndexL3(sbL2));
        StringBuilder sbL4 = GrammerUtils.insertSet(sbL3, setL4, GrammerUtils.getIndexL4(sbL3));

        return sbL4.toString();
    }


    public static String[] resArray(int resId) {
        return NovelApp.getInstance().getApplicationContext().getResources().getStringArray(resId);
    }
}
