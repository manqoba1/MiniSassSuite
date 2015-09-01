package com.sifiso.codetribe.minisasslibrary.dto.tranfer;


import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.CommentDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ConditionsDTO;
import com.sifiso.codetribe.minisasslibrary.dto.CountryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ErrorStoreAndroidDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ErrorStoreDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.StreamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TmemberDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aubreyM
 */
public class RequestDTO implements Serializable {

    public static final int REGISTER_TEAM = 1,
            SIGN_IN_MEMBER = 3,
            REGISTER_TEAM_MEMBER = 2,
            DECLINE_MEMBER = 12,
            GET_PASSWORD = 92;


    public static final int ADD_RIVER = 10,
            IMPORT_MEMBER_INFO = 13,
            ADD_INSECT_IMAGE = 14,
            ADD_EVALUATION = 15,
            ADD_COMMENT = 16,
            ADD_EVALUATION_INSECT = 17,
            ADD_TEAM = 18,
            ADD_STREAM = 19;


    public static final int ADD_COUNTRY = 21,
            ADD_PROVINCE = 22,
            ADD_TOWN = 23;

    public static final int UPDATE_RIVER = 30,
            UPDATE_RIVER_TOWN = 31,
            UPDATE_EVALUATION_SITE = 32,
            UPDATE_PROFILE = 33,
            UPDATE_INSECT_IMAGE = 34,
            UPDATE_TOWN = 35,
            UPDATE_TEAM = 36,
            UPDATE_TEAM_MEMBER = 37,
            UPDATE_COMMENT = 38,
            UPDATE_EVALUATION = 39,
            UPDATE_CONDITIONS = 55,
            SEND_INVITE_TO_TEAM_MEMBER = 56;

    public static final int LIST_RIVERS_IN_COUNTRY = 40,
            LIST_RIVER_TOWNS = 41,
            LIST_EVALUATION_SITES = 42,
            SEARCH_MEMBERS = 43,
            GET_MEMBER = 44,
            LIST_EVALUATION_SITE_BY_RIVER = 45,
            LIST_PROVINCE_BY_COUNTRY = 46,
            LIST_REGISTER_DATA = 50,
            LIST_EVALUATION_BY_TEAM_MEMBER = 60,
            LIST_EVALUATION_BY_CONDITIONS = 61,
            LIST_EVALUATION_SITE_BY_CATEGORY = 62,
            LIST_EVALUATION_INSECT_BY_EVALUATION = 63,
            LIST_TEAMS_BY_TOWN = 64,
            LIST_TEAM_MEMBERS = 65,
            LIST_TOWN_BY_PROVINCE = 66,
            LIST_CATEGORY = 67,
            LIST_COMMENTS = 68,
            LIST_COUNTRYS = 69,
            LIST_EVALUATIONS = 70,
            LIST_RIVERS = 71,
            GET_DATA = 72,
            LIST_ALL_RIVER_TOWNS = 73,
            LIST_DATA_WITH_RADIUS_RIVERS = 75,
            LIST_EVALUATION_SITE_WITH_RADIUS = 76,
            LIST_OF_INSECTS_IMAGES = 77,
            LIST_EVALUATION_BY_RIVER_ID = 78,
            ADD_GCM_DEVICE = 79,
            GET_RIVERS_BY_RADIUS = 100;


    public static final String SAMPLE_DIR = "company";
    public static final String EVALUATION_DIR = "evaluation";
    public static final String INSECTS_DIR = "insert";
    public static final String TEAM_DIR = "team";
    public static final String TEAM_MEMBER_DIR = "teamMember";
    private int requestType, radius;
    private String email, password;
    private String countryCode, teamMemberImage, search;
    private Integer countryID, categoryID, commentID, conditionsID, evaluationID, evaluationInsectID,
            evaluationCommentID, evaluationSiteID, insectID, provinceID, riverID, teamID, townID, teamMemberID,
            evaluationImageID;
    private Double latitude, longitude;
    private Float accuracy;
    private ImagesDTO images;
    private ErrorStoreAndroidDTO errorStoreAndroid;
    private ErrorStoreDTO errorStore;
    private GcmDeviceDTO gcmDevice;
    private EvaluationDTO evaluation;
    private CategoryDTO category;
    private ConditionsDTO conditions;
    private EvaluationImageDTO evaluationImage;
    private EvaluationInsectDTO evaluationInsect;
    private TeamDTO team;
    private TeamMemberDTO teamMember;
    private RiverDTO river;
    private EvaluationSiteDTO evaluationSite;
    private CommentDTO comment;
    private CountryDTO country;
    private InsectDTO insect;
    private InsectImageDTO insectImage;
    private StreamDTO stream;
    private TmemberDTO tmember;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public StreamDTO getStream() {
        return stream;
    }

    public void setStream(StreamDTO stream) {
        this.stream = stream;
    }

    public TmemberDTO getTmember() {
        return tmember;
    }

    public void setTmember(TmemberDTO tmember) {
        this.tmember = tmember;
    }

    public String getTeamMemberImage() {
        return teamMemberImage;
    }

    public void setTeamMemberImage(String teamMemberImage) {
        this.teamMemberImage = teamMemberImage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private List<TeamMemberDTO> members;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    private List<InsectImageDTO> insectImages = new ArrayList<>();

    public List<InsectImageDTO> getInsectImages() {
        return insectImages;
    }

    public void setInsectImages(List<InsectImageDTO> insectImages) {
        this.insectImages = insectImages;
    }

    public RequestDTO(Integer requestType) {
        this.requestType = requestType;
    }

    public RequestDTO() {

    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ImagesDTO getImages() {
        return images;
    }

    public void setImages(ImagesDTO images) {
        this.images = images;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public ConditionsDTO getConditions() {
        return conditions;
    }

    public void setConditions(ConditionsDTO conditions) {
        this.conditions = conditions;
    }

    public ErrorStoreAndroidDTO getErrorStoreAndroid() {
        return errorStoreAndroid;
    }

    public void setErrorStoreAndroid(ErrorStoreAndroidDTO errorStoreAndroid) {
        this.errorStoreAndroid = errorStoreAndroid;
    }

    public ErrorStoreDTO getErrorStore() {
        return errorStore;
    }

    public void setErrorStore(ErrorStoreDTO errorStore) {
        this.errorStore = errorStore;
    }

    public EvaluationImageDTO getEvaluationImage() {
        return evaluationImage;
    }

    public void setEvaluationImage(EvaluationImageDTO evaluationImage) {
        this.evaluationImage = evaluationImage;
    }

    public EvaluationInsectDTO getEvaluationInsect() {
        return evaluationInsect;
    }

    public void setEvaluationInsect(EvaluationInsectDTO evaluationInsect) {
        this.evaluationInsect = evaluationInsect;
    }

    public GcmDeviceDTO getGcmDevice() {
        return gcmDevice;
    }

    public void setGcmDevice(GcmDeviceDTO gcmDevice) {
        this.gcmDevice = gcmDevice;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public List<TeamMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMemberDTO> members) {
        this.members = members;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }

    public InsectDTO getInsect() {
        return insect;
    }

    public void setInsect(InsectDTO insect) {
        this.insect = insect;
    }

    public InsectImageDTO getInsectImage() {
        return insectImage;
    }

    public void setInsectImage(InsectImageDTO insectImage) {
        this.insectImage = insectImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public TeamMemberDTO getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(TeamMemberDTO teamMember) {
        this.teamMember = teamMember;
    }

    public RiverDTO getRiver() {
        return river;
    }

    public void setRiver(RiverDTO river) {
        this.river = river;
    }

    public EvaluationSiteDTO getEvaluationSite() {
        return evaluationSite;
    }

    public void setEvaluationSite(EvaluationSiteDTO evaluationSite) {
        this.evaluationSite = evaluationSite;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
    }

    public Integer getConditionsID() {
        return conditionsID;
    }

    public void setConditionsID(Integer conditionsID) {
        this.conditionsID = conditionsID;
    }

    public Integer getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Integer evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Integer getEvaluationInsectID() {
        return evaluationInsectID;
    }

    public void setEvaluationInsectID(Integer evaluationInsectID) {
        this.evaluationInsectID = evaluationInsectID;
    }

    public Integer getEvaluationCommentID() {
        return evaluationCommentID;
    }

    public void setEvaluationCommentID(Integer evaluationCommentID) {
        this.evaluationCommentID = evaluationCommentID;
    }

    public Integer getEvaluationSiteID() {
        return evaluationSiteID;
    }

    public void setEvaluationSiteID(Integer evaluationSiteID) {
        this.evaluationSiteID = evaluationSiteID;
    }

    public Integer getInsectID() {
        return insectID;
    }

    public void setInsectID(Integer insectID) {
        this.insectID = insectID;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }

    public Integer getRiverID() {
        return riverID;
    }

    public void setRiverID(Integer riverID) {
        this.riverID = riverID;
    }

    public Integer getTownID() {
        return townID;
    }

    public void setTownID(Integer townID) {
        this.townID = townID;
    }

    public Integer getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamMemberID(Integer teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public Integer getEvaluationImageID() {
        return evaluationImageID;
    }

    public void setEvaluationImageID(Integer evaluationImageID) {
        this.evaluationImageID = evaluationImageID;
    }

}
